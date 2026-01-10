package com.checkmate.matches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchApplication.class, args);
	}

}
