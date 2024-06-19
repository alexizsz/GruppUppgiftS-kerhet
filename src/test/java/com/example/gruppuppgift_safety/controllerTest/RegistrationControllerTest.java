package com.example.gruppuppgift_safety.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Indikerar att detta är en Spring Boot-test
@AutoConfigureMockMvc // Konfigurerar MockMvc automatiskt för test
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Injicerar en instans av MockMvc för att köra MVC-test

    // Testfall för framgångsrik registrering
    @Test
    public void testSuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/register") // Gör ett HTTP POST-request till "/register"
                        .param("name", "testUser") // Skickar parametern "name" med värdet "testUser"
                        .param("password", "password")) // Skickar parametern "password" med värdet "password"
                .andExpect(status().is3xxRedirection()) // Förväntar sig att HTTP-status är en 3xx omdirigering
                .andExpect(redirectedUrl("/regSuccessful")); // Förväntar sig att omdirigeringen går till "/regSuccessful"
    }

    // Testfall för misslyckad registrering på grund av existerande användarnamn
    @Test
    public void testFailedRegistration() throws Exception {
        mockMvc.perform(post("/register") // Gör ett HTTP POST-request till "/register"
                        .param("name", "existingUser") // Skickar parametern "name" med värdet "existingUser"
                        .param("password", "password")) // Skickar parametern "password" med värdet "password"
                .andExpect(status().isOk()) // Förväntar sig att HTTP-status är 200 (OK)
                .andExpect(model().attributeExists("error")) // Förväntar sig att modellen har attributet "error"
                .andExpect(view().name("register")); // Förväntar sig att vyn är "register"
    }

    // Testfall för autentiserad användare som försöker registrera en ny användare
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void testAuthenticatedRegistration() throws Exception {
        mockMvc.perform(post("/register") // Gör ett HTTP POST-request till "/register"
                        .param("name", "newUser") // Skickar parametern "name" med värdet "newUser"
                        .param("password", "password")) // Skickar parametern "password" med värdet "password"
                .andExpect(status().is3xxRedirection()) // Förväntar sig att HTTP-status är en 3xx omdirigering
                .andExpect(redirectedUrl("/regSuccessful")); // Förväntar sig att omdirigeringen går till "/regSuccessful"
    }
}