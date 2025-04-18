package com.gestion.plus.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class })
@EntityScan(basePackages = { "com.gestion.plus.commons" })
@EnableJpaAuditing
public class GestionPlusApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionPlusApiApplication.class, args);
	}
}
