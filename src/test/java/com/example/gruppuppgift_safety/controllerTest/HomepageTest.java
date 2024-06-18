package com.example.gruppuppgift_safety.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HomepageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHomepageUnauthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(result -> assertEquals("http://localhost/login", result.getResponse().getRedirectedUrl()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHomepageAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

}
