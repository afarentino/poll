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
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CsvFileService implements AnswersRepository, Closeable {

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
    private BufferedWriter writer;
    private CSVFormat format;
    private CSVPrinter printer;

    @Autowired
    private CsvFileService(String fileName) {
        this.csvPath = FileSystems.getDefault().getPath(fileName);
    }

    @Override
    public synchronized void reset() {
        if (csvPath.toFile().exists()) {
            logger.info("Prior answer file detected. Removing it.");
            csvPath.toFile().delete();
        }
        init();
    }
    @Override
    public synchronized void init() {
        logger.info("Initializing the CSVFileService");
        try {
            //TODO: check that the file is not zero bytes
            if (csvPath.toFile().exists()) {
                this.format = CSVFormat.EXCEL.builder().setHeader(HEADERS).setSkipHeaderRecord(true).build();
            }
            else {
                this.format = CSVFormat.EXCEL.builder().setHeader(HEADERS).build();
            }
            this.writer = Files.newBufferedWriter(this.csvPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            this.printer = new CSVPrinter(writer, format);
        } catch (IOException e) {
            throw new RuntimeException("Failed to init FileService", e);
        }
    }
    private String timeStamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    public synchronized void save(Questions data) {
        try {
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

            printer.printRecord(timeStamp(),
                    data.getFirstName(),
                    data.getLastName(),
                    data.getEmail(),
                    timeDayMap.get("6pm Week 1"),
                    timeDayMap.get("6:30pm Week 1"),
                    timeDayMap.get("7pm Week 1"),
                    timeDayMap.get("7:30pm Week 1"),
                    timeDayMap.get("6pm Week 2"),
                    timeDayMap.get("6:30pm Week 2"),
                    timeDayMap.get("7pm Week 2"),
                    timeDayMap.get("7:30pm Week 2"));

            // Flush results to file immediately while we hold the write lock
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Resource answersCsv() {
        try {
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

    @Override
    public synchronized void close() throws IOException {
        this.writer.close();
        this.printer.close(true);
    }

    @PreDestroy
    public void destroy() {
        logger.info("Callback triggered - @PreDestroy CsvFileService.");
        try {
            this.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
