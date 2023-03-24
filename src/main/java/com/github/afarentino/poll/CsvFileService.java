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
import java.io.Closeable;
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
            "6pm Week 1", "6:30pm Week 1", "7pm Week 1", "7:30pm Week 1",
            "6pm Week 2", "6:30pm Week 2", "7pm Week 2",
            "7:30pm Week 2" };

    private static final String[] DAYS = { "Mon", "Tue", "Wed", "Thu", "Fri" };
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
    private void store(Questions data, Map<String,String> timeDayMap) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        // Build the list of timeSlots
        for (Map.Entry<String,String> entry: timeDayMap.entrySet()) {
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
        String[][] checkedDays = data.getCheckboxes();
        Map<String,String> timeDayMap = new LinkedHashMap<>();

        for (int i = 0; i < checkedDays.length; i++ ) {
            String timeSlot = data.timeAt(i);
            String days = "";
            for (int j = 0; j < checkedDays[i].length; j++) {
                if (checkedDays[i][j] != null) {
                    days += (days.isEmpty()) ? checkedDays[i][j] : ", " + checkedDays[i][j];
                }
            }
            // Add timeSlot entry to map
            timeDayMap.put(timeSlot, days);
        }
        store(data, timeDayMap);
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
                        timeDayMap.get("6pm Week 1"),
                        timeDayMap.get("6:30pm Week 1"),
                        timeDayMap.get("7pm Week 1"),
                        timeDayMap.get("7:30pm Week 1"),
                        timeDayMap.get("6pm Week 2"),
                        timeDayMap.get("6:30pm Week 2"),
                        timeDayMap.get("7pm Week 2"),
                        timeDayMap.get("7:30pm Week 2"));

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
