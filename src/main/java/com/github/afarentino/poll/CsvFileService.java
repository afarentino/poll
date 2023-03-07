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

@Service
public class CsvFileService implements AnswersRepository, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(CsvFileService.class);
    private static Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final String fileSeparator = File.separator;

    private static final String[] HEADERS = { "Timestamp",
            "First Name",
            "Last Name",
            "Email",
            "6pm Week1", "6:30pm Week1", "7pm Week1", "7:30pm Week1",
            "6pm Week2", "6:30pm Week2", "7pm Week2",
            "7:30pm Week2" };

    private static final String[] DAYS = { "Mon", "Tue", "Wed", "Thu", "Fri" };
    private Path csvPath;
    private BufferedWriter writer;
    private CSVFormat format;
    private CSVPrinter printer;

    @Autowired
    private CsvFileService(String fileName) {
        init(fileName);
    }

    private synchronized void init(String fileName) {
        this.csvPath = FileSystems.getDefault().getPath(fileName);

        //TODO: Allow reset all to work here...
        if (csvPath.toFile().exists()) {
            logger.info("Prior answer file detected. Removing it.");
            csvPath.toFile().delete();
        }
        logger.info("Creating a new CSV file header");
        this.format = CSVFormat.EXCEL.builder().setHeader(HEADERS).build();
        try {
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
            printer.printRecord(timeStamp(), data.getFirstName(), data.getLastName(), data.getEmail());
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
