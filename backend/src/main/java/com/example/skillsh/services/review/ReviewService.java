package com.example.skillsh.services.review;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ReviewService{
    Optional<Review> findById(Long aLong);
    List<Review> findReviewByReviewedUser(User user);

    void addReview(AddReviewRequest request);

    List<Review> getReviewsByReviewedUser_Username(String username);
    void updateReview(Long id, String newContent);
    void deleteReview(Long id);

    // Не забравяй да обновиш и интерфейса ReviewService.java да приема 3 параметъра!
    void replyToReview(Long reviewId, String replyContent, String authorUsername);
}
