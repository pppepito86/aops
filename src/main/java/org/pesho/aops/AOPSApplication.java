package org.pesho.aops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class AOPSApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AOPSApplication.class, args);
	}
	
}
