package com.example.skillsh.service;

import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.Role;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.comment.CommentService;
import com.example.skillsh.services.review.ReviewService;
import com.example.skillsh.services.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private ReviewService reviewService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testUnblockUser_Success() {
        // Arrange
        String blockerUsername = "user1";
        String blockedUsername = "user2";

        User blocker = new User();
        blocker.setUsername(blockerUsername);

        User blocked = new User();
        blocked.setUsername(blockedUsername);

        // Добавяме блокирания потребител в списъка на блокиращия
        List<User> blockedUsersList = new ArrayList<>();
        blockedUsersList.add(blocked);
        blocker.setBlockedUsers(blockedUsersList);

        when(userRepo.findUserByUsername(blockerUsername)).thenReturn(Optional.of(blocker));
        when(userRepo.findUserByUsername(blockedUsername)).thenReturn(Optional.of(blocked));

        // Act
        userService.unblockUser(blockerUsername, blockedUsername);

        // Assert
        // Уверяваме се, че списъкът с блокирани вече е празен
        assertTrue(blocker.getBlockedUsers().isEmpty());
        verify(userRepo, times(1)).save(blocker);
    }

    @Test
    void testGetBlockStatus_WhenUserExists() {
        // Arrange
        String targetUsername = "target_user";
        User targetUser = new User();
        targetUser.setId(1L);
        targetUser.setUsername(targetUsername);

        User blockedUser = new User();
        blockedUser.setUsername("bad_user");

        User blockerUser = new User();
        blockerUser.setUsername("admin_user");

        // targetUser е блокирал bad_user
        targetUser.setBlockedUsers(List.of(blockedUser));

        when(userRepo.findUserByUsername(targetUsername)).thenReturn(Optional.of(targetUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(targetUser));

        // admin_user е блокирал targetUser
        when(userRepo.findUsersWhoBlocked(targetUser)).thenReturn(List.of(blockerUser));

        // Act
        Map<String, List<String>> status = userService.getBlockStatus(targetUsername);

        // Assert
        assertNotNull(status);
        assertEquals(1, status.get("blocked").size());
        assertEquals("bad_user", status.get("blocked").get(0));

        assertEquals(1, status.get("blockedBy").size());
        assertEquals("admin_user", status.get("blockedBy").get(0));
    }

    @Test
    void testGetUsersWithReviews_FiltersOutAdminsAndFetchesComments() {
        // Arrange
        User admin = new User();
        admin.setRole(Set.of(new Role("ADMIN")));

        User expert = new User();
        expert.setUsername("expert1");
        expert.setRole(Set.of(new Role("EXPERT")));

        when(userRepo.findAll()).thenReturn(List.of(admin, expert));

        Review review = new Review();
        review.setReviewText("Супер!");

        when(reviewService.findReviewByReviewedUser(expert)).thenReturn(List.of(review));
        when(commentService.findCommentByReview(review)).thenReturn(List.of()); // Връща празен списък с коментари

        // Act
        List<User> result = userService.getUsersWithReviews();

        // Assert
        assertEquals(1, result.size()); // Админът трябва да бъде филтриран
        assertEquals("expert1", result.get(0).getUsername());
        assertEquals(1, result.get(0).getReview().size());
        assertEquals("Супер!", result.get(0).getReview().get(0).getReviewText());

        verify(reviewService, times(1)).findReviewByReviewedUser(expert);
        verify(commentService, times(1)).findCommentByReview(review);
    }
}



