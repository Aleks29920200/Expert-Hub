package com.example.skillsh.domain.view;

import com.example.skillsh.domain.dto.review.ReviewCommentDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private List<ReviewCommentDTO> replies;

    private String targetUsername;
    private String reviewerUsername;

    // Текстът на ревюто (в Angular го пращаш като 'content')
    private String content;

    // ВАЖНО: Тук държим само String с името на автора, а НЕ целия обект User!
    private String authorUsername;




}
