package com.example.skillsh.controllers;



import com.example.skillsh.services.chat.BlockService;
import com.example.skillsh.web.BlockController;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlockController.class)
@AutoConfigureMockMvc(addFilters = false)
class BlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlockService blockService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testBlockUser_ShouldReturn200() throws Exception {
        // Arrange
        Map<String, String> payload = Map.of(
                "blocker", "user1",
                "blocked", "user2"
        );

        // Act & Assert
        mockMvc.perform(post("/api/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("User blocked successfully."));

        // Проверяваме дали сървисът е извикан с точните параметри
        verify(blockService, times(1)).blockUser("user1", "user2");
    }

    @Test
    void testUnblockUser_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/blocks")
                        .param("blocker", "user1")
                        .param("blocked", "user2"))
                .andExpect(status().isOk())
                .andExpect(content().string("User unblocked successfully."));

        verify(blockService, times(1)).unblockUser("user1", "user2");
    }

    @Test
    void testIsBlocked_ShouldReturnBoolean() throws Exception {
        // Arrange
        when(blockService.isBlocked("user1", "user2")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/blocks/is-blocked")
                        .param("source", "user1")
                        .param("target", "user2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // Връщаме JSON boolean като string
    }
}


