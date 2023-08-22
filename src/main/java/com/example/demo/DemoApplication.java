package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

@SpringBootApplication
@RestController
public class DemoApplication {

	@RequestMapping("/")
    public String home(Model model) {
        return "index";
    }
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}