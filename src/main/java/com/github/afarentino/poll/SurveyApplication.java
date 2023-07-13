package com.github.afarentino.poll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = EntryRepository.class)
public class SurveyApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(SurveyApplication.class);

	public static void main(String[] args) {
		// Inject MongoDB URL from environment
		SpringApplication.run(SurveyApplication.class, args);
	}
    @Value("${app.answers.file}")
	private String answerFile;
	@Autowired
	EntryRepository entryStore;

	private boolean adminResetAll = false;
	@Bean
	public String csvFileName() {
		return this.answerFile;
	}

	@Bean
	CommandLineRunner init(CsvFileService fileService, EntryRepository entryStore) {
		return (args) -> {
			if (this.adminResetAll) {
				entryStore.deleteAll();  // Delete all prior saved survey results
				logger.info("Survey storage deleted");
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
