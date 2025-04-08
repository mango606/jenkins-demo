package com.example.jenkins_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/")
    public String home() {
        return "Jenkins CI/CD 데모 애플리케이션에 오신 것을 환영합니다!";
    }

    @GetMapping("/version")
    public String version() {
        return "애플리케이션 버전: 1.0.0";
    }
}