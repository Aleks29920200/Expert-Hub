package com.example.skillsh.controllers;



import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.services.review.ReviewService;
import com.example.skillsh.web.ReviewController;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserDetailsService userDetailsService;

    // --- УСПЕШНИ СЦЕНАРИИ ---

    @Test
    void testAddReview_Success() throws Exception {
        AddReviewRequest request = new AddReviewRequest();

        mockMvc.perform(post("/api/reviews/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review added successfully!"));
    }

    @Test
    void testUpdateReview_Success() throws Exception {
        Map<String, String> payload = Map.of("content", "Ново съдържание");

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ревюто е обновено успешно!"));
    }

    @Test
    void testDeleteReview_Success() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ревюто е изтрито успешно!"));
    }

    @Test
    void testReplyToReview_Success() throws Exception {
        Map<String, String> payload = Map.of(
                "content", "Благодаря за ревюто!",
                "authorUsername", "ivan_expert"
        );

        mockMvc.perform(post("/api/reviews/1/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Отговорът е добавен успешно!"));
    }

    // --- СЦЕНАРИИ С ГРЕШКИ (Хващане на catch блоковете) ---

    @Test
    void testAddReview_Exception_Returns500() throws Exception {
        AddReviewRequest request = new AddReviewRequest();
        doThrow(new RuntimeException("Грешка при базата")).when(reviewService).addReview(any());

        mockMvc.perform(post("/api/reviews/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Грешка при базата"));
    }

    @Test
    void testDeleteReview_Exception_Returns400() throws Exception {
        doThrow(new RuntimeException("Ревюто не е намерено")).when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ревюто не е намерено"));
    }
}


