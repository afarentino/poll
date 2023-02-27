package com.github.afarentino.datepicker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SurveyApplication {
	private static final Logger logger = LoggerFactory.getLogger(SurveyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SurveyApplication.class, args);
	}

}
