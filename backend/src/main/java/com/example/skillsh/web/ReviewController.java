package com.example.skillsh.web;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.dto.review.ReviewCommentDTO;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.view.ReviewDTO;
import com.example.skillsh.services.review.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody AddReviewRequest request) {
        try {
            // Извикваме сървиса
            reviewService.addReview(request);

            // Връщаме успешен отговор към Angular (200 OK)
            return ResponseEntity.ok().body("{\"message\": \"Review added successfully!\"}");
        } catch (Exception e) {
            // Връщаме грешка към Angular (500 Internal Server Error)
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/user/{username}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable String username) {

        // 1. Взимаме ревютата от базата
        List<Review> reviews = reviewService.getReviewsByReviewedUser_Username(username);

        // 2. Преобразуваме всяко Review в ReviewDTO
        List<ReviewDTO> reviewDTOs = reviews.stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();

            // Основна информация за главното ревю
            dto.setId(review.getId());
            dto.setContent(review.getReviewText());
            dto.setAuthorUsername(review.getReviewingUser().getUsername());

            // 3. НОВОТО: Обработваме нишката от отговори (списъка с ReviewComment)
            if (review.getReplies() != null) {
                List<ReviewCommentDTO> commentDTOs = review.getReplies().stream().map(comment -> {
                    ReviewCommentDTO commentDTO = new ReviewCommentDTO();
                    commentDTO.setId(comment.getId());
                    commentDTO.setContent(comment.getContent());
                    commentDTO.setAuthorUsername(comment.getAuthorUsername());
                    commentDTO.setCreated(comment.getCreated());
                    return commentDTO;
                }).collect(Collectors.toList());

                // Закачаме масива с отговори към DTO-то
                dto.setReplies(commentDTOs);
            } else {
                // Застраховка: ако няма отговори, пращаме празен списък, за да не гърми Angular
                dto.setReplies(new ArrayList<>());
            }

            return dto;
        }).collect(Collectors.toList());

        // 4. Връщаме готовия списък към фронтенда
        return ResponseEntity.ok(reviewDTOs);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String newContent = payload.get("content");
            reviewService.updateReview(id, newContent);
            return ResponseEntity.ok().body("{\"message\": \"Ревюто е обновено успешно!\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // 2. ИЗТРИВАНЕ НА РЕВЮ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().body("{\"message\": \"Ревюто е изтрито успешно!\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // 3. ОТГОВОР НА РЕВЮ
    @PostMapping("/{id}/reply")
    public ResponseEntity<?> replyToReview(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            // Взимаме текста и автора от JSON обекта, който Angular праща
            String replyContent = payload.get("content");
            String authorUsername = payload.get("authorUsername");

            reviewService.replyToReview(id, replyContent, authorUsername);
            return ResponseEntity.ok().body("{\"message\": \"Отговорът е добавен успешно!\"}");
        } catch (Exception e) {
            // Ако гръмне тук, ще върне 400 Bad Request с текст на грешката
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
