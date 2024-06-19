package com.example.gruppuppgift_safety.controllerTest;

import com.example.gruppuppgift_safety.controller.RegistrationController;
import com.example.gruppuppgift_safety.controller.UserManagementController;
import com.example.gruppuppgift_safety.utility.HtmlUtil;
import com.example.gruppuppgift_safety.utility.MaskingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

/**
 * Testklass för att testa UserManagementController.
 */
@SpringBootTest // Använder Spring Boot-testkontexten
@AutoConfigureMockMvc // Konfigurerar MockMvc automatiskt för test
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc; // Injicerar en instans av MockMvc för att köra MVC-test

    @Mock
    private RegistrationController registrationController; // Mock för RegistrationController

    @Mock
    private InMemoryUserDetailsManager userDetailsManager; // Mock för InMemoryUserDetailsManager

    @Mock
    private MaskingUtils maskingUtils; // Mock för MaskingUtils

    @Mock
    private HtmlUtil htmlUtil; // Mock för HtmlUtil

    @InjectMocks
    private UserManagementController userManagementController; // Instans av UserManagementController att testa

    /**
     * Metod som körs innan varje testfall för att konfigurera testmiljön.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Öppnar mockarna för annotationer
        mockMvc = MockMvcBuilders.standaloneSetup(userManagementController).build(); // Konfigurerar MockMvc med UserManagementController
    }

    /**
     * Testfall för att testa framgångsrik borttagning av användare.
     */
    @Test
    @WithMockUser(roles = "ADMIN") // Simulerar en inloggad användare med ADMIN-roll
    public void testDeleteUser_SuccessfulDeletion() throws Exception {
        String username = "testUser";
        String email = "test@example.com";
        Map<String, String> userEmails = new HashMap<>();
        userEmails.put(username, email);

        when(registrationController.getUserEmails()).thenReturn(userEmails); // Mockar getUserEmails-metoden i RegistrationController
        doNothing().when(userDetailsManager).deleteUser(username); // Mockar deleteUser-metoden i userDetailsManager

        mockMvc.perform(MockMvcRequestBuilders.post("/manageuser/delete")
                        .param("username", username)
                        .param("email", email)
                        .with(csrf())) // Simulerar en POST-request med CSRF-skydd
                .andExpect(redirectedUrl("/manageuser")); // Förväntar sig att användaren omdirigeras till /manageuser

        verify(userDetailsManager, times(1)).deleteUser(username); // Verifierar att deleteUser-metoden anropas exakt en gång
    }

    /**
     * Testfall för att testa misslyckad borttagning av användare på grund av e-postmismatch.
     *
     * @throws Exception om testet misslyckas
     */
    @Test
    @WithMockUser(roles = "ADMIN") // Simulerar en inloggad användare med ADMIN-roll
    public void testDeleteUser_FailureDueToEmailMismatch() throws Exception {
        String username = "testUser";
        String email = "wrong@example.com";
        Map<String, String> userEmails = new HashMap<>();
        userEmails.put(username, null); // Lägger till null eftersom e-posten inte matchar

        when(registrationController.getUserEmails()).thenReturn(userEmails); // Mockar getUserEmails-metoden i RegistrationController

        mockMvc.perform(MockMvcRequestBuilders.post("/manageuser/delete")
                        .param("username", username)
                        .param("email", email)
                        .with(csrf())) // Simulerar en POST-request med CSRF-skydd
                .andExpect(redirectedUrl("/manageuser?errorUsername=" + username)); // Förväntar sig att användaren omdirigeras med felmeddelande

        verify(userDetailsManager, never()).deleteUser(username); // Verifierar att deleteUser-metoden inte anropas
    }
}
