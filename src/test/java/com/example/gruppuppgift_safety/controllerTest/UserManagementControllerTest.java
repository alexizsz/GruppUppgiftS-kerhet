package com.example.gruppuppgift_safety.controllerTest;

import com.example.gruppuppgift_safety.controller.RegistrationController;
import com.example.gruppuppgift_safety.controller.UserManagementController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testklass för UserManagementController.
 * Denna klass innehåller integrationstester som verifierar beteendet av användarhanteringsfunktionaliteten.
 * Testerna använder MockMvc för att simulera HTTP-förfrågningar och kontrollera svaren.
 */
@WebMvcTest(UserManagementController.class) // Indikerar att det är en Spring MVC-test endast för UserManagementController
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc-objekt som används för att simulera HTTP-förfrågningar till controller

    @MockBean
    private InMemoryUserDetailsManager userDetailsManager; // Mockad instans av InMemoryUserDetailsManager

    @MockBean
    private RegistrationController registrationController; // Mockad instans av RegistrationController

    /**
     * Förberedelser innan varje testfall.
     * Här mockas beteendet för registrationController och userDetailsManager för att simulera
     * vissa scenarier relaterade till användarhantering.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Mocka beteendet för registrationController
        // Skapa en mockad Map som håller användarens e-postadresser för att simulera registrationController
        Map<String, String> mockUserEmails = new HashMap<>();
        mockUserEmails.put("testuser", "testuser@example.com");
        // Mockar beteendet för registrationController att returnera mockUserEmails när getUserEmails anropas
        Mockito.when(registrationController.getUserEmails()).thenReturn(mockUserEmails);

        // Mocka beteendet för userDetailsManager (skapar en UserDetails för testen)
        UserDetails user = User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        // Reflekterande åtkomst till "users"-fältet i InMemoryUserDetailsManager för att sätta mock-data
        Field field = InMemoryUserDetailsManager.class.getDeclaredField("users");
        field.setAccessible(true); // Gör fältet tillgängligt för att kunna ändra det, även om det är privat
        // Skapar en ny HashMap som håller användardetaljer för att simulera userDetailsManager
        Map<String, UserDetails> usersMap = new HashMap<>();
        usersMap.put("testuser", user); // Lägger till en användare "testuser" och användardetaljerna i usersMap
        field.set(userDetailsManager, usersMap); // Sätter värdet av fältet "users" i userDetailsManager till usersMap
    }

    /**
     * Testfall för autentiserad åtkomst till användarhanteringssidan.
     * Testet simulerar en inloggad admin-användare och verifierar att sidan returnerar
     * HTTP 200 OK samt att modellattributen "users" och "userEmails" existerar.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserManagementPageAuthenticated() throws Exception {
        mockMvc.perform(get("/manageuser"))
                .andExpect(status().isOk()) // Verifiera att HTTP-status är 200 OK
                .andExpect(view().name("manageuser")) // Verifiera att vyn är "manageuser"
                .andExpect(model().attributeExists("users")) // Verifiera att "users" attribut existerar i modellen
                .andExpect(model().attributeExists("userEmails")); // Verifiera att "userEmails" attribut existerar i modellen
    }

    @Test
    void getUserManagementPageUnauthenticated() throws Exception {
        mockMvc.perform(get("/manageuser"))
                .andExpect(status().isUnauthorized()); // Verifiera att HTTP-status är 401 Unauthorized för o-autentiserad åtkomst
    }

    /**
     * Testfall för o-autentiserad åtkomst till användarhanteringssidan.
     * Testet verifierar att sidan returnerar HTTP 401 Unauthorized när en användare
     * försöker nå sidan utan autentisering.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUserSuccess() throws Exception {

        mockMvc.perform(post("/manageuser/delete")
                        .param("username", "testuser")
                        .param("email", "testuser@example.com")
                        .with(csrf())) // Inkluderar CSRF-token
                .andExpect(status().isOk()) // Verifiera att HTTP-status är 200 OK
                .andExpect(view().name("deleteUserSuccessful")); // Verifiera att vyn är "deleteUserSuccessful"
    }

    /**
     * Testfall för framgångsrik borttagning av användare.
     * Testet simulerar en POST-förfrågan till "/manageuser/delete" med korrekta parametrar för att
     * ta bort en användare och verifierar att HTTP 200 OK returneras samt att rätt vy visas.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUserFailure() throws Exception {
        mockMvc.perform(post("/manageuser/delete")
                        .param("username", "testuser")
                        .param("email", "wrongemail@example.com")
                        .with(csrf())) // Inkluderar CSRF-token
                .andExpect(status().is3xxRedirection()) // Verifiera att HTTP-status är en 3xx redirection
                .andExpect(redirectedUrl("/manageuser?errorUsername=testuser")); // Verifiera att omdirigerad URL är korrekt
    }
}
