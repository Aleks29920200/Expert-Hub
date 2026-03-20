package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.ReviewComment;
import com.example.skillsh.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {


   List<Review> findReviewByReviewedUser(User user);

   Optional<Review> findById(Long aLong);
   List<Review>getReviewsByReviewedUser_Username(String username);
}


