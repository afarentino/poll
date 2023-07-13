package com.github.afarentino.poll;

import jakarta.annotation.PreDestroy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvFileService implements AnswersRepository {

    private static final Logger logger = LoggerFactory.getLogger(CsvFileService.class);
    private static Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final String fileSeparator = File.separator;

    private static final String[] HEADERS = { "Timestamp",
            "First Name",
            "Last Name",
            "Email",
            "8/5 (Sat)", "8/6 (Sun)", "8/12 (Sat)", "8/13 (Sun)", "8/19 (Sat)", "8/20 (Sun)", "8/26 (Sat)",
            "8/27 (Sun)", "9/2 (Sat)", "9/3 (Sun)", "9/9 (Sat)", "9/10 (Sun)", "9/16 (Sat)", "9/17 (Sun)" };

    private Path csvPath;
    private CSVFormat format;

    private EntryRepository entryStore;

    @Autowired
    private CsvFileService(String fileName, EntryRepository entryStore) {
        this.csvPath = FileSystems.getDefault().getPath(fileName);
        this.entryStore = entryStore;
    }

    @Override
    public synchronized void reset() {
        if (csvPath.toFile().exists()) {
            logger.info("Prior answer file detected. Removing it.");
            csvPath.toFile().delete();
        }
    }
    @Override
    public synchronized void init() {
        logger.info("Initializing the CSVFileService");
        reset();
        this.format = CSVFormat.EXCEL.builder().setHeader(HEADERS).build();
    }
    private String timeStamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    private void store(Questions data, Map<String,String> keyValueMap) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        // Build the list of timeSlots
        for (Map.Entry<String,String> entry: keyValueMap.entrySet()) {
            TimeSlot current = new TimeSlot(entry.getKey(), entry.getValue());
            timeSlots.add(current);
        }

        final Entry userEntry = new Entry(timeStamp(),
                                           data.getFirstName(),
                                           data.getLastName(),
                                           data.getEmail(), timeSlots);
        entryStore.save(userEntry);
    }
    public synchronized void save(Questions data) {
        String[][] checkedValues = data.getCheckboxes();
        Map<String,String> keyValueMap = new LinkedHashMap<>();

        for (int i = 0; i < checkedValues.length; i++ ) {
            String timeSlot = data.keyAt(i);
            String values = "";
            for (int j = 0; j < checkedValues[i].length; j++) {
                if (checkedValues[i][j] != null) {
                    values += (values.isEmpty()) ? checkedValues[i][j] : ", " + checkedValues[i][j];
                }
            }
            // Add timeSlot entry to map
            keyValueMap.put(timeSlot, values);
        }
        store(data, keyValueMap);
    }

    void appendEntries() {
        List<Entry> entries = this.entryStore.findAll();

        try (BufferedWriter writer = Files.newBufferedWriter(this.csvPath,
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (Entry e : entries) {
                Map<String, String> timeDayMap = new LinkedHashMap<>();
                List<TimeSlot> slots = e.getTimeSlots();
                for (TimeSlot s : slots) {
                    timeDayMap.put(s.getHeader(), s.getValue());
                }

                printer.printRecord(e.getTimeStamp(),
                        e.getFirstName(),
                        e.getLastName(),
                        e.getEmail(),
                        timeDayMap.get("8/5 (Sat)"),   // 0
                        timeDayMap.get("8/6 (Sun)"),   // 1
                        timeDayMap.get("8/12 (Sat)"),  // 2
                        timeDayMap.get("8/13 (Sun)"),  // 3
                        timeDayMap.get("8/19 (Sat)"),  // 4
                        timeDayMap.get("8/20 (Sun)"),  // 5
                        timeDayMap.get("8/26 (Sat)"),  // 6
                        timeDayMap.get("8/27 (Sun)"),  // 7
                        timeDayMap.get("9/2 (Sat)"),   // 8
                        timeDayMap.get("9/3 (Sun)"),   // 9
                        timeDayMap.get("9/9 (Sat)"),   // 10
                        timeDayMap.get("9/10 (Sun)"),  // 11
                        timeDayMap.get("9/16 (Sat)"),  // 12
                        timeDayMap.get("9/17 (Sun)")); // 13

                // Flush results to file immediately...
                printer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to append stored entries", e);
        }

    }
    @Override
    public synchronized Resource answersCsv() {
        try {
            //TODO -> Generate the File on the fly based on contents found in MongoDB
            init();
            appendEntries();
            Resource resource = new UrlResource(this.csvPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalStateException("Attempt to fetch CSV from unreadable path");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read answers CSV file", e);
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("Callback triggered - @PreDestroy CsvFileService.");
        this.reset();
    }
}
