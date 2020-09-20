package org.zwx.configuration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    @Value("tangseng")
    private String name;

    @Value("${value.int}")
    private int valueInt;

    @Value("${value.float}")
    private float valueFloat;

    @Value("${value.string}")
    private String valueString;

    @Value("${value.bool}")
    private boolean valueBool;

    @Value("${value.double:2.22}")
    private double valueDouble;

    @Value("${value.time}")
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration time;

    @Autowired
    private Environment env;

    @Autowired
    private Student student;

    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/helloValue")
    public Object helloValue() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("valueInt", valueInt);
        result.put("valueFloat", valueFloat);
        result.put("valueString", valueString);
        result.put("valueBool", valueBool);
        result.put("valueDouble", valueDouble);
        result.put("time", time.getSeconds() + "s");
        result.put("JAVA_HOME", env.getProperty("JAVA_HOME"));
        result.put("GRADLE_HOME", env.getProperty("GRADLE_HOME"));
        result.put("NO_ENV", env.getProperty("NO_ENV", "no env variable"));
        return result;
    }

    @GetMapping("/getStudent")
    public Student getStudent() {
        return student;
    }
}
