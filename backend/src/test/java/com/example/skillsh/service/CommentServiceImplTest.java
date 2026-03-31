package com.example.skillsh.service;

import com.example.skillsh.domain.entity.Comment;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.CommentRepo;
import com.example.skillsh.services.comment.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepo commentRepo;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void testAddComment() {
        // Arrange
        User user = new User();
        user.setUsername("testAuthor");

        Review review = new Review();
        review.setId(1L);

        String commentText = "Много полезно!";

        // Act
        commentService.addComment(user, commentText, review);

        // Assert
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepo, times(1)).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertEquals("testAuthor", savedComment.getAuthor().getUsername());
        assertEquals(commentText, savedComment.getText());
        assertEquals(review, savedComment.getReview());
        assertNotNull(savedComment.getDate());
        assertEquals(LocalDate.now(), savedComment.getDate());
    }

    @Test
    void testFindCommentByReview() {
        // Arrange
        Review review = new Review();
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();

        when(commentRepo.findCommentByReview(review)).thenReturn(List.of(comment1, comment2));

        // Act
        List<Comment> result = commentService.findCommentByReview(review);

        // Assert
        assertEquals(2, result.size());
        verify(commentRepo, times(1)).findCommentByReview(review);
    }
}


