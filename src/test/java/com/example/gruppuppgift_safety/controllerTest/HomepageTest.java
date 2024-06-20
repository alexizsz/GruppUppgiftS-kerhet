package com.example.gruppuppgift_safety.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testklass för att verifiera åtkomsten till hemsidan ("/").
 * Klassen innehåller två testfall:
 * 1. Ett testfall för att verifiera att o-autentiserade användare inte kan komma åt hemsidan och istället får en 401 Unauthorized status.
 * 2. Ett testfall för att verifiera att autentiserade användare med rollen "USER" kan komma åt hemsidan och får en 200 OK status.
 */
@SpringBootTest // Indikerar att detta är en Spring Boot-test
@AutoConfigureMockMvc // Konfigurerar MockMvc automatiskt för test
public class HomepageTest {

    @Autowired
    private MockMvc mockMvc; // Injicerar en instans av MockMvc för att köra MVC-test och verifiera svar utan att starta en fullständig webbserver (Line 23-24)

    /**
     * Testfall för o-autentiserad åtkomst till första-sidan.
     * Verifierar att o-autentiserade användare får en 401 Unauthorized status när de försöker komma åt hemsidan.
     */
    @Test
    public void testHomepageUnauthenticated() throws Exception {
        mockMvc.perform(get("/")) // Gör ett HTTP GET-request till "/"
                .andExpect(status().isUnauthorized()); // // Förväntar sig HTTP 401 Unauthorized
    }

    /**
     * Testfall för autentiserad åtkomst till första-sidan med användarrollen "USER".
     * Verifierar att autentiserade användare med rollen "USER" får en 200 OK status när de försöker komma åt hemsidan.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"}) // Simulerar en inloggad user-användare
    public void testHomepageAuthenticated() throws Exception {
        mockMvc.perform(get("/")) // Gör ett HTTP GET-request till "/"
                .andExpect(status().isOk()); // Förväntar sig att HTTP-status är 200 (OK)
    }

}