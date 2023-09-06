package com.github.afarentino.poll;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers("/", "/survey", "/css/**", "/js/**", "/images/**", "/scripts/**").permitAll()
                        .anyRequest().authenticated().and().oauth2Login();
        return http.build();
    }
}

