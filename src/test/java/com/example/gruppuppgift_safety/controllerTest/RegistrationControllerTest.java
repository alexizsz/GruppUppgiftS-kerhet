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

/**
 * Testklass för RegistrationController.
 * Denna klass innehåller integrationstester som verifierar beteendet av registreringsändpunkten.
 * Testerna använder MockMvc för att simulera HTTP-förfrågningar och kontrollera svaren.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc-objekt som används för att simulera HTTP-förfrågningar till controller

    /**
     * Testfall för framgångsrik registrering.
     * Detta test simulerar ett registreringsförsök med matchande lösenord och verifierar att
     * registreringen är framgångsrik och att rätt vy returneras.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simulerar en inloggad admin-användare
    public void testSuccessfulRegistration() throws Exception {
        // Skapa parametrar för registreringsformuläret
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "newUser");
        params.add("password", "newPassword");
        params.add("confirmPassword", "newPassword");

        // Utför en POST-förfrågan till /register-ändpunkten med formulärparametrarna och en CSRF-token
        mockMvc.perform(post("/register")
                        .params(params)
                        .with(csrf())) // Inkluderar CSRF-token
                .andExpect(status().isOk()) // Förväntar sig en HTTP 200 OK-status
                .andExpect(view().name("register")); // Förväntar sig att vy-namnet är "register"
    }

    /**
     * Testfall för misslyckad registrering på grund av lösenordsmatchning.
     * Detta test simulerar ett registreringsförsök med icke-matchande lösenord och verifierar att
     * registreringen misslyckas och att rätt felmeddelande visas.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testRegistrationFailedPasswordMismatch() throws Exception {
        // Skapa parametrar för registreringsformuläret med icke-matchande lösenord
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "newUser");
        params.add("password", "newPassword");
        params.add("confirmPassword", "wrongPassword");

        // Utför en POST-förfrågan till /register-ändpunkten med formulärparametrarna och en CSRF-token
        mockMvc.perform(post("/register")
                        .params(params)
                        .with(csrf())) // Inkluderar CSRF-token
                .andExpect(status().isOk()) // Förväntar sig en HTTP 200 OK-status, eftersom vi inte förväntar oss en 400
                .andExpect(view().name("register")) // Förväntar sig att vy-namnet är "register" igen på grund av valideringsfel
                .andExpect(model().attributeExists("error")) // Förväntar sig en "error" attribut i modellen
                .andExpect(model().attribute("error", "There are errors in the form, please correct them")); // Förväntar sig det specifika felmeddelandet
    }

}
