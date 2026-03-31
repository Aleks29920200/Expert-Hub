package com.example.skillsh.web;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.dto.review.ReviewAdminDto;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.services.review.AdminReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReviewAdminDto>> getAllReviews() {
        return ResponseEntity.ok(adminReviewService.getAllReviews());
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewAdminDto> createReview(@RequestBody AddReviewRequest dto) {
        return ResponseEntity.ok(adminReviewService.createReview(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody AddReviewRequest dto) {
        return ResponseEntity.ok(adminReviewService.updateReview(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        adminReviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
