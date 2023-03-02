package com.github.afarentino.poll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SurveyApplication {
	private static final Logger logger = LoggerFactory.getLogger(SurveyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SurveyApplication.class, args);
	}
    @Value("${app.answers.file}")
	private String answerFile;

	@Bean
	public String csvFileName() {
		return this.answerFile;
	}
	@Bean
	CommandLineRunner init(FileService fileService) {
		return (args) -> {
			logger.info("Application initialization complete. CSV file in use is " + answerFile);
		};
	}

}
