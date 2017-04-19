package com.cloud.ops.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Nathan on 2017/4/19.
 */
@SpringBootApplication
public class TestApplication {


    public static void main(String[] args) {
        new SpringApplication(TestApplication.class).run(args);
    }

}