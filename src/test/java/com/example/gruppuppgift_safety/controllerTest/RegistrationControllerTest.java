package com.example.gruppuppgift_safety.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
                .andExpect(status().isOk()); // Förväntar sig en HTTP 200 OK-status
    }

    // Testfall för misslyckad registrering på grund av lösenordsmatchning
    @Test
    public void testRegistrationFailedPasswordMismatch() throws Exception {
        // Förbered en POST-förfrågan till endpoint "/register" med parametrarna användarnamn, lösenord och bekräfta lösenord
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .param("username", "newUser")
                .param("password", "newPassword")
                .param("confirmPassword", "wrongPassword");

        // Utför förfrågan mot MockMvc och förvänta dig ett svar
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

}