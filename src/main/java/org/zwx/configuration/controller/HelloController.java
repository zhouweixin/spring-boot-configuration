package org.zwx.configuration.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/helloValue")
    public Object helloValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("valueInt", valueInt);
        map.put("valueFloat", valueFloat);
        map.put("valueString", valueString);
        map.put("valueBool", valueBool);
        map.put("valueDouble", valueDouble);
        return map;
    }
}
