package org.zwx.configuration.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"value.int=10", "value.float=10.10"}, locations = {"/a.properties"})
class TestPropertyResource2Test {
    @Autowired
    MockMvc mockMvc;

    @Test
    void helloValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/helloValue"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueInt", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueFloat", Matchers.is(10.10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueString", Matchers.is("shaheshang")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valueBool", Matchers.is(true)));
    }
}