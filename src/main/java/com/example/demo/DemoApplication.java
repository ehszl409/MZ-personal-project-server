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
		return "<div style=\"position: relative; width: 100%; height: 0; padding-top: 56.25%; overflow: hidden; will-change: transform;\">\r\n" + //
				"            <iframe loading=\"lazy\" style=\"position: absolute; width: 100%; height: 100%; top: 0; left: 0; border: none; padding: 0; margin: 0;\" src=\"https:&#x2F;&#x2F;www.miricanvas.com&#x2F;v&#x2F;12c5vx2?embed\">\r\n" + //
				"            </iframe>\r\n" + //
				"        </div>";
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}