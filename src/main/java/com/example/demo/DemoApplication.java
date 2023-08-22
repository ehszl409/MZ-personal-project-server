package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

	@RequestMapping("/")
	String home() {
		return 
		"<!DOCTYPE html>\n" +
		"<html>\n" +
		"<head>\n" +
		"    <title>Welcome to My Demo Application</title>\n" +
		"    <style>\n" +
		"        body {\n" +
		"            font-family: Arial, sans-serif;\n" +
		"            background-color: #f0f0f0;\n" +
		"            margin: 0;\n" +
		"            padding: 0;\n" +
		"            display: flex;\n" +
		"            flex-direction: column;\n" +
		"            align-items: center;\n" +
		"            justify-content: center;\n" +
		"            height: 100vh;\n" +
		"        }\n" +
		"        h1 {\n" +
		"            color: #333;\n" +
		"        }\n" +
		"        p {\n" +
		"            font-size: 18px;\n" +
		"            color: #666;\n" +
		"        }\n" +
		"    </style>\n" +
		"</head>\n" +
		"<body>\n" +
		"    <h1>Hello Woong World</h1>\n" +
		"    <p>Welcome to my demo application built with Spring Boot!</p>\n" +
		"</body>\n" +
		"</html>";
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}