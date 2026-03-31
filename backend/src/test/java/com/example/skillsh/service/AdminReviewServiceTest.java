package com.example.skillsh.service;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.dto.review.ReviewAdminDto;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.ReviewRepo;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.review.AdminReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminReviewServiceTest {

    @Mock
    private ReviewRepo reviewRepository;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminReviewService adminReviewService;

    @Test
    void testAddReview_Success() {
        // Arrange
        AddReviewRequest dto = new AddReviewRequest();
        dto.setContent("Страхотен експерт!");
        dto.setReviewerUsername("user_reviewer");
        dto.setTargetUsername("user_target");

        User reviewer = new User();
        reviewer.setUsername("user_reviewer");

        User target = new User();
        target.setUsername("user_target");

        when(userRepo.findUserByUsername("user_reviewer")).thenReturn(Optional.of(reviewer));
        when(userRepo.findUserByUsername("user_target")).thenReturn(Optional.of(target));

        Review savedReview = new Review();
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // Act
        ReviewAdminDto result = adminReviewService.createReview(dto);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(1)).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertEquals("Страхотен експерт!", capturedReview.getReviewText());
        assertEquals(reviewer, capturedReview.getReviewingUser());
        assertEquals(target, capturedReview.getReviewedUser());
    }

    @Test
    void testAddReview_ThrowsExceptionWhenReviewerNotFound() {
        // Arrange
        AddReviewRequest dto = new AddReviewRequest();
        dto.setReviewerUsername("ghost_user");

        when(userRepo.findUserByUsername("ghost_user")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            adminReviewService.createReview(dto);
        });

        assertEquals("Потребителят (оценяващ) не е намерен!", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void testDeleteReview_Success() {
        // Arrange
        when(reviewRepository.existsById(1L)).thenReturn(true);

        // Act
        adminReviewService.deleteReview(1L);

        // Assert
        verify(reviewRepository, times(1)).deleteById(1L);
    }
}
