package com.example.skillsh.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="reviews")
@NoArgsConstructor
public class Review {

    @Id
    @Column(name="review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "reviewing_user_id")
    private User reviewingUser;

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id")
    private User reviewedUser;
    @Column(columnDefinition = "TEXT")
    private String reviewText;
    private int rating;
    private LocalDate dateOfReview;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> replies = new ArrayList<>();




}


