package com.example.skillsh.controllers;



import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.web.AuthController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Контролерът ползва директно Repository, затова мокваме него
    @MockBean
    private UserRepo userRepo;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testLogin_Success_ShouldReturnToken() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "username", "testuser",
                "password", "password123"
        );

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123"); // В реален проект тук е хеширана парола

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token-for-testuser"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testLogin_WrongPassword_ShouldReturn401() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "username", "testuser",
                "password", "wrongpassword"
        );

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Грешна парола!"));
    }

    @Test
    void testGetCurrentUser_WithValidHeader_ShouldReturnUserAndRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                        // Подаваме Authorization хедър, както би го направил браузърът
                        .header("Authorization", "Bearer test-token-for-admin_peter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin_peter"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void testGetCurrentUser_MissingHeader_ShouldReturn401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/me")) // Без хедър
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Липсващ токен"));
    }
}