package com.example.gruppuppgift_safety.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Indikerar att detta är en Spring Boot-test
@AutoConfigureMockMvc // Konfigurerar MockMvc automatiskt för test
public class HomepageTest {

    @Autowired
    private MockMvc mockMvc; // Injicerar en instans av MockMvc för att köra MVC-test

    // Testfall för o-autentiserad åtkomst till första-sidan
    @Test
    public void testHomepageUnauthenticated() throws Exception {
        mockMvc.perform(get("/")) // Gör ett HTTP GET-request till "/"
                .andExpect(status().isUnauthorized()); // // Förväntar sig HTTP 401 Unauthorized
    }

    // Testfall för autentiserad åtkomst till första-sidan med användarrollen "USER"
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHomepageAuthenticated() throws Exception {
        mockMvc.perform(get("/")) // Gör ett HTTP GET-request till "/"
                .andExpect(status().isOk()); // Förväntar sig att HTTP-status är 200 (OK)
    }

}