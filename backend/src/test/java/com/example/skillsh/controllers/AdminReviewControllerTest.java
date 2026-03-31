package com.example.skillsh.controllers;


import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.dto.review.ReviewAdminDto;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.services.review.AdminReviewService;
import com.example.skillsh.web.AdminReviewController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AdminReviewController.class)
@AutoConfigureMockMvc(addFilters = false) // Отново изключваме защитите за теста
class AdminReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminReviewService adminReviewService;
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllReviews_ShouldReturn200AndList() throws Exception {
        // Arrange (Подготовка)
        ReviewAdminDto dto = new ReviewAdminDto();
        dto.setReviewText("Отлична работа!");
        dto.setReviewingUserUsername("ivan_ivanov");
        dto.setReviewedUserUsername("petar_kaluchov");

        when(adminReviewService.getAllReviews()).thenReturn(List.of(dto));

        // Act & Assert (Изпълнение и проверка)
        mockMvc.perform(get("/api/admin/reviews/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].reviewText").value("Отлична работа!"))
                .andExpect(jsonPath("$[0].reviewerUsername").value("ivan_ivanov"));

        verify(adminReviewService, times(1)).getAllReviews();
    }

    @Test
    void testCreateReview_ShouldReturn200AndCreatedReview() throws Exception {
        // Arrange
        AddReviewRequest requestDto = new AddReviewRequest();
        requestDto.setContent("Много съм доволен.");
        requestDto.setReviewerUsername("ivan_ivanov");
        requestDto.setTargetUsername("expert_peter");

        ReviewAdminDto responseDto = new ReviewAdminDto();
        responseDto.setId(1L);
        responseDto.setReviewText("Много съм доволен.");
        responseDto.setReviewingUserUsername("ivan_ivanov");
        responseDto.setReviewedUserUsername("petar_kaluchov");

        when(adminReviewService.createReview(any(AddReviewRequest.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/admin/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // Превръщаме обекта в JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reviewText").value("Много съм доволен."));

        verify(adminReviewService, times(1)).createReview(any(AddReviewRequest.class));
    }

    @Test
    void testUpdateReview_ShouldReturn200() throws Exception {
        // Arrange
        Long reviewId = 1L;
        AddReviewRequest requestDto = new AddReviewRequest();
        requestDto.setContent("Корекция на ревюто: Добро отношение.");

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setReviewText("Корекция на ревюто: Добро отношение.");

        // Използваме eq(reviewId), за да сме сигурни, че подаваме точното ID към мокнатия метод
        when(adminReviewService.updateReview(eq(reviewId), any(AddReviewRequest.class)))
                .thenReturn(updatedReview);

        // Act & Assert
        mockMvc.perform(put("/api/admin/reviews/update/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewText").value("Корекция на ревюто: Добро отношение."));

        verify(adminReviewService, times(1)).updateReview(eq(reviewId), any(AddReviewRequest.class));
    }

    @Test
    void testDeleteReview_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/reviews/delete/{id}", 1L))
                .andExpect(status().isOk());

        verify(adminReviewService, times(1)).deleteReview(1L);
    }
}