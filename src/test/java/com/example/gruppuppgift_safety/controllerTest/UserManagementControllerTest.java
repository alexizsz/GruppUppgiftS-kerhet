package com.example.gruppuppgift_safety.controllerTest;

import com.example.gruppuppgift_safety.controller.UserManagementController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testklass för att testa UserManagementController.
@SpringBootTest
@AutoConfigureMockMvc
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserManagementController userManagementController; // Instans av UserManagementController att testa

    // Metod som körs innan varje testfall för att konfigurera testmiljön.
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Öppnar mockarna för annotationer
        mockMvc = MockMvcBuilders.standaloneSetup(userManagementController).build(); // Konfigurerar MockMvc med UserManagementController
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser_SuccessfulDeletion() throws Exception {
        mockMvc.perform(post("/delete").param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser_NotFound() throws Exception {
        mockMvc.perform(post("/delete").param("email", "wrong@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_not_found"));
    }
}
