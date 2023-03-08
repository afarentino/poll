package com.github.afarentino.poll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SurveyApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(SurveyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SurveyApplication.class, args);
	}
    @Value("${app.answers.file}")
	private String answerFile;

	private boolean adminResetAll = false;
	@Bean
	public String csvFileName() {
		return this.answerFile;
	}
	@Bean
	CommandLineRunner init(CsvFileService fileService) {
		return (args) -> {
			if (this.adminResetAll) {
				fileService.reset();
			} else {
				fileService.init();
			}
			logger.info("Application initialization complete. CSV file in use is " + answerFile);
		};
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("Processing application command line args");
		if ( args.containsOption("resetAll")) {
			List<String> values = args.getOptionValues("resetAll");
			if (values.isEmpty() == false) {
				throw new IllegalStateException("--resetAll is a no argument option flag");
			}
			this.adminResetAll = true;
		}
	}

}
