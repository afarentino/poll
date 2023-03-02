package com.github.afarentino.poll;

import jakarta.annotation.PreDestroy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileService implements AnswersRepository, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final String fileSeparator = File.separator;

    private static final String[] HEADERS = { "First Name", "Last Name", "Email" };
    private Path csvPath;
    private BufferedWriter writer;
    private CSVFormat format;
    private CSVPrinter printer;

    @Autowired
    private FileService(String fileName) {
        init(fileName);
    }

    private void init(String fileName) {
        this.csvPath = FileSystems.getDefault().getPath(fileName);

        //TODO: Allow reset all to work here...
        if (csvPath.toFile().exists()) {
            logger.info("Prior answer file detected. Removing it.");
            csvPath.toFile().delete();
        }
        logger.info("Creating a new CSV file header");
        this.format = CSVFormat.DEFAULT.builder().setHeader(HEADERS).build();
        try {
            this.writer = Files.newBufferedWriter(this.csvPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            this.printer = new CSVPrinter(writer, format);
        } catch (IOException e) {
            throw new RuntimeException("Failed to init FileService", e);
        }
    }
    synchronized public void save(Questions data) {
        try {
            printer.printRecord(data.firstName, data.lastName, data.email);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.printer.close();
    }

    @PreDestroy
    public void destroy() {
        logger.info("Callback triggered - @PreDestroy.");
        try {
            this.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
