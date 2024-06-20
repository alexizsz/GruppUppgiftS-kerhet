package com.example.gruppuppgift_safety.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Testfall för framgångsrik registrering
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testSuccessfulRegistration() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "newUser");
        params.add("password", "newPassword");
        params.add("confirmPassword", "newPassword");

        mockMvc.perform(post("/register")
                        .params(params)
                        .with(csrf())) // Inkluderar CSRF-token
                .andExpect(status().isOk()) // Förväntar sig en HTTP 200 OK-status
                .andExpect(view().name("register")); // Förväntar sig att visa "registrationSuccess" view
    }

    // Testfall för misslyckad registrering på grund av lösenordsmatchning
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testRegistrationFailedPasswordMismatch() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "newUser");
        params.add("password", "newPassword");
        params.add("confirmPassword", "wrongPassword");

        // Använd BindingResult direkt för att simulera en felaktig validering
        mockMvc.perform(post("/register")
                        .params(params)
                        .with(csrf())) // Inkluderar CSRF-token
                .andExpect(status().isOk()) // Förväntar sig en HTTP 200 OK-status, eftersom vi inte förväntar oss en 400
                .andExpect(view().name("register")) // Förväntar sig att visa "registration" view igen
                .andExpect(model().attributeExists("error")) // Kontrollera att felet finns i modellen
                .andExpect(model().attribute("error", "There are errors in the form, please correct them")); // Kontrollera felmeddelandet
    }

}
