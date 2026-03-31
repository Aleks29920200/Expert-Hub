package com.example.skillsh.services.review;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.ReviewComment;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.ReviewRepo;
import com.example.skillsh.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private ReviewRepo repo;
    private UserRepo userRepo;
@Autowired
    public ReviewServiceImpl(ReviewRepo repo, UserRepo userRepo) {
        this.repo = repo;
    this.userRepo = userRepo;
}

    @Override
    public Optional<Review> findById(Long aLong) {
        return this.repo.findById(aLong);
    }

    @Override
    public List<Review> findReviewByReviewedUser(User user) {
        return this.repo.findReviewByReviewedUser(user);
    }

    @Override
    public void addReview(AddReviewRequest request) {

        // 1. Намираме автора
        User reviewer = userRepo.findUserByUsername(request.getReviewerUsername()).orElse(null);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found!");
        }

        // 2. Намираме експерта
        User targetUser = userRepo.findUserByUsername(request.getTargetUsername()).orElse(null);
        if (targetUser == null) {
            throw new RuntimeException("Target user not found!");
        }

        // 3. Създаваме и запазваме
        Review review = new Review();
        review.setReviewingUser(reviewer);
        review.setReviewedUser(targetUser);
        review.setReviewText(request.getContent());
        review.setDateOfReview(request.getDate());

        repo.save(review);
    }

    @Override
    public List<Review> getReviewsByReviewedUser_Username(String username) {
        return this.repo.getReviewsByReviewedUser_Username(username);
    }

    @Override
    public void updateReview(Long id, String newContent) {
        Review review = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ревюто не е намерено!"));

        review.setReviewText(newContent);
        repo.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Ревюто не е намерено!");
        }
        repo.deleteById(id);
    }

    // Не забравяй да обновиш и интерфейса ReviewService.java да приема 3 параметъра!
    @Override
    @Transactional
    public void replyToReview(Long reviewId, String replyContent, String authorUsername) {
        Review review = repo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Ревюто не е намерено!"));

        ReviewComment comment = new ReviewComment();
        comment.setContent(replyContent);
        comment.setAuthorUsername(authorUsername);
        comment.setReview(review); // Свързваме коментара с главното ревю

        review.getReplies().add(comment);
        repo.save(review);
    }
}


