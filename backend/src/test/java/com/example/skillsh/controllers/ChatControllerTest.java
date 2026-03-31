package com.example.skillsh.controllers;

import com.example.skillsh.services.message.MessageService;
import com.example.skillsh.web.ChatController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;
import java.util.Map;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    // Мокваме Principal (текущо логнатия потребител), за да не гърми Spring Security
    private final Principal mockPrincipal = () -> "test_user";

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetRecentContacts_ShouldReturnList() throws Exception {
        // Arrange
        String username = "test_user";
        List<Map<String, String>> mockContacts = List.of(
                Map.of("contact", "ivan", "lastMessage", "Здрасти!")
        );

        when(messageService.findRecentContactsForUser(username)).thenReturn(mockContacts);

        // Act & Assert
        mockMvc.perform(get("/api/chat/contacts/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].contact").value("ivan"));
    }

    @Test
    void testVideoCall_ShouldReturnModelAndView() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/user/videochat/{userId}", 5L)
                        .principal(mockPrincipal)) // Подаваме мокнатия логнат потребител
                .andExpect(status().isOk())
                .andExpect(view().name("video-call")) // Проверяваме дали връща правилния HTML темплейт
                .andExpect(model().attribute("username", "test_user"))
                .andExpect(model().attribute("userId", 5L));
    }
}


