package com.fujitsu.fooddelivery.feeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// @EnableScheduling
public class FeeApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeeApplication.class, args);
	}
}