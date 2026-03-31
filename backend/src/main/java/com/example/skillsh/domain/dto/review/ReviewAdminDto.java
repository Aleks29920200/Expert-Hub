package com.example.skillsh.domain.dto.review;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReviewAdminDto {
    private Long id;
    private String reviewingUserUsername; // Име на този, който пише
    private String reviewedUserUsername;  // Име на този, за когото е ревюто
    private String reviewText;
    private int rating;
    private LocalDate dateOfReview;
}