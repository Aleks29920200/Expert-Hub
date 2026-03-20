package com.example.skillsh.domain.dto.review;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddReviewRequest {
        private String reviewerUsername;
        private String targetUsername; // Вече е String, а не Long targetId!
        private String content;
        private LocalDate date;

        // Генерирай Getters и Setters (или използвай @Data от Lombok, ако имаш)
        public String getReviewerUsername() { return reviewerUsername; }
        public void setReviewerUsername(String reviewerUsername) { this.reviewerUsername = reviewerUsername; }

        public String getTargetUsername() { return targetUsername; }
        public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}


