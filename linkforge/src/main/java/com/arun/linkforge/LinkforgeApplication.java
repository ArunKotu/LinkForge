package com.arun.linkforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.arun.linkforge")
public class LinkforgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkforgeApplication.class, args);
	}

}
