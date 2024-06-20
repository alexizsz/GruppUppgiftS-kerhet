package com.example.gruppuppgift_safety.controllerTest;

import com.example.gruppuppgift_safety.controller.RegistrationController;
import com.example.gruppuppgift_safety.controller.UserManagementController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InMemoryUserDetailsManager userDetailsManager;

    @MockBean
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the behavior of the registrationController
        Map<String, String> mockUserEmails = new HashMap<>();
        mockUserEmails.put("testuser", "testuser@example.com");
        Mockito.when(registrationController.getUserEmails()).thenReturn(mockUserEmails);

        // Mock the behavior of the userDetailsManager
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();
        Mockito.when(userDetailsManager.loadUserByUsername("testuser")).thenReturn(user);

        // Mock the users field in InMemoryUserDetailsManager
        Field field = InMemoryUserDetailsManager.class.getDeclaredField("users");
        field.setAccessible(true);
        Map<String, UserDetails> usersMap = new HashMap<>();
        usersMap.put("testuser", user);
        field.set(userDetailsManager, usersMap);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserManagementPageAuthenticated() throws Exception {
        mockMvc.perform(get("/manageuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("manageuser"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("userEmails"));
    }

    @Test
    void getUserManagementPageUnauthenticated() throws Exception {
        mockMvc.perform(get("/manageuser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUserSuccess() throws Exception {
        Mockito.doNothing().when(userDetailsManager).deleteUser("testuser");

        mockMvc.perform(post("/manageuser/delete")
                        .param("username", "testuser")
                        .param("email", "testuser@example.com")
                        .with(csrf()))
                .andExpect(status().isOk()) // Uppdatera förväntan till 200 OK
                .andExpect(view().name("deleteUserSuccessful")); // Kontrollera att view name är korrekt
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUserFailure() throws Exception {
        mockMvc.perform(post("/manageuser/delete")
                        .param("username", "testuser")
                        .param("email", "wrongemail@example.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manageuser?errorUsername=testuser"));
    }
}
