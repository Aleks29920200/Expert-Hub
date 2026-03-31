package com.example.skillsh.service;

import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.repository.ReviewRepo;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.review.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepo reviewRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void testUpdateReview_Success() {
        // Arrange
        Long reviewId = 1L;
        String newContent = "Ново съдържание на ревюто";
        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setReviewText("Старо съдържание");

        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(existingReview));

        // Act
        reviewService.updateReview(reviewId, newContent);

        // Assert
        assertEquals(newContent, existingReview.getReviewText());
        verify(reviewRepo, times(1)).save(existingReview);
    }

    @Test
    void testReplyToReview_Success() {
        // Arrange
        Long reviewId = 5L;
        String replyContent = "Благодаря за отзива!";
        String authorUsername = "expert_user";

        Review existingReview = new Review();
        existingReview.setId(reviewId);

        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(existingReview));

        // Act
        reviewService.replyToReview(reviewId, replyContent, authorUsername);

        // Assert
        // Проверяваме дали коментарът е добавен към списъка с коментари на ревюто
        assertEquals(1, existingReview.getComments().size());
        assertEquals(replyContent, existingReview.getComments().get(0).getText());
        assertEquals(authorUsername, existingReview.getComments().get(0).getAuthor());

        // Уверяваме се, че обновеното ревю е записано в базата
        verify(reviewRepo, times(1)).save(existingReview);
    }

    @Test
    void testReplyToReview_ThrowsExceptionWhenReviewNotFound() {
        // Arrange
        when(reviewRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reviewService.replyToReview(99L, "Отговор", "admin");
        });

        assertEquals("Ревюто не е намерено!", ex.getMessage());
        verify(reviewRepo, never()).save(any());
    }
}


