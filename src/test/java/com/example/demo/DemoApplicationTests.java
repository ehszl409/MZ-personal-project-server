package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private DemoApplication demoApplication;

    @Test
    void testHome() {
        String result = demoApplication.home(null);  // Pass null for Model

        assertEquals("index", result);
    }
}
