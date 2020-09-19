package org.zwx.configuration.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void hello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("Hello world!")));
    }

    @Test
    void helloValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/helloValue"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("tangseng")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueInt", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueFloat", Matchers.is(1.11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueString", Matchers.is("sunwukong")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueBool", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueDouble", Matchers.is(2.22)))
        ;
    }
}