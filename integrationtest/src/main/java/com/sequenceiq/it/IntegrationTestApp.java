package com.sequenceiq.it;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testng.TestNG;

@SpringBootApplication
public class IntegrationTestApp implements CommandLineRunner {

    @Autowired
    private TestNG testng;

    @Override
    public void run(String... args) {
//        TestListenerAdapter tla = new TestListenerAdapter();
        System.setProperty("spring.freemarker.checkTemplateLocation", "false");
        testng.setTestSuites(Arrays.asList(args));
//        testng.addListener(tla);
        testng.run();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(IntegrationTestApp.class, args);
    }
}
