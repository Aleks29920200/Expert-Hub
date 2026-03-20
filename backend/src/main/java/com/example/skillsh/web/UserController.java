package com.example.skillsh.web;

import com.example.skillsh.domain.dto.user.AddUserDTO;
import com.example.skillsh.domain.dto.user.UserDTO;
import com.example.skillsh.domain.entity.*;
import com.example.skillsh.domain.entity.enums.Status;
import com.example.skillsh.domain.view.ReviewDTO;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.chat.BlockService;
import com.example.skillsh.services.comment.CommentService;
import com.example.skillsh.services.review.ReviewService;
import com.example.skillsh.services.user.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final BlockService blockService;

    @Autowired
    public UserController(UserService userService, ReviewService reviewService, CommentService commentService, BlockService blockService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.commentService = commentService;
        this.blockService = blockService;
    }

    @MessageMapping("/user.setUserStatus")
    @SendTo("/user-public")
    public User addUser(@Payload User user) {
        User existing = userService.findUserByUsername(user.getUsername()).orElse(null);
        if (existing == null) {
            user.setActivity(Status.ONLINE);
            return userService.setUserStatus(user);
        } else {
            existing.setActivity(Status.ONLINE);
            return userService.setUserStatus(existing);
        }
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user-public")
    public User disconnectUser(@Payload User user) {
        User existing = userService.findUserByUsername(user.getUsername()).orElse(null);
        if (existing != null) {
            existing.setActivity(Status.OFFLINE);
            return userService.setUserStatus(existing);
        }
        return user;
    }

    @GetMapping("/connected")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.getAll());
    }





    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long reviewId,
                                        @RequestBody Map<String, String> payload,
                                        Principal principal) {
        String text = payload.get("text");
        Review review = reviewService.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        User user = userService.findUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        this.commentService.addComment(user, text, review);
        return ResponseEntity.ok("Comment added successfully");
    }

    // --- OLD BLOCK ENDPOINTS DELETED FROM HERE ---

    @GetMapping("/admin/all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserView>> getAllUsersForAdmin(Principal principal) {
        List<UserView> users = new ArrayList<>();
        userService.users().forEach(e -> {
            if (principal == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            if (!(e.getUsername().equals(principal.getName()))) {
                UserView map = mapper.map(e, UserView.class);
                users.add(map);
            }
        });
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsersForChat() {
        try {
            List<User> users = userService.users().stream().map(e->mapper.map(e,User.class)).toList();
            List<User> safeUsers = users.stream().map(user -> {
                User dto = new User();
                dto.setUsername(user.getUsername());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(safeUsers);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    // Преобразуваме сложния User обект в прост и безопасен UserView
                    UserView userView = mapper.map(user, UserView.class);

                    // Връщаме го опакован по същия начин, по който го очаква Angular
                    return ResponseEntity.ok(Map.of("userProfile", userView));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/admin/user-details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserDetailsAdmin(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/add-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUserAdmin(@Valid @RequestBody AddUserDTO addUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        this.userService.addUser(addUserDto);
        return ResponseEntity.ok("User added successfully");
    }

    @PutMapping("/admin/edit-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUserAdmin(@PathVariable Long id, @RequestBody AddUserDTO userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/admin/remove-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUserAdmin(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User removed successfully");
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> viewUserByUsername(@PathVariable String username) {
        return userService.findUserByUsername(username)
                .map(user -> {
                    // 1. Мапваме основните полета (id, username, email и т.н.)
                    UserView userView = mapper.map(user, UserView.class);

                    // 2. Взимаме ревютата за този потребител (както правеше в оригиналния си код)
                    List<Review> rawReviews = reviewService.findReviewByReviewedUser(user);

                    // 3. Ръчно ги превръщаме в ReviewDTO, за да няма грешки
                    if (rawReviews != null) {
                        List<ReviewDTO> reviewDTOs = rawReviews.stream().map(review -> {
                            ReviewDTO dto = new ReviewDTO();
                            // Провери как точно се казва get метода за текста при теб (напр. getText() или getDescription())
                            dto.setContent(review.getReviewText());

                            // Взимаме името на автора (Провери дали е getAuthor() или getReviewer() при теб)
                            if (review.getReviewingUser() != null) {
                                dto.setAuthorUsername(review.getReviewingUser().getUsername());
                            }
                            return dto;
                        }).toList(); // за Java 16+ (или .collect(Collectors.toList()) за по-стари)

                        // 4. Слагаме готовите DTO-та в профила
                        userView.setReviews(reviewDTOs);
                    }

                    return ResponseEntity.ok(Map.of("userProfile", userView));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/clients")
    @Transactional
    public ResponseEntity<List<Map<String, Object>>> getAllClients() {
        // Взимаме всички потребители
        List<User> allUsers = userService.getAll();

        // Филтрираме само клиентите и ги превръщаме в безопасен, прост обект (Map)
        List<Map<String, Object>> clientsOnly = allUsers.stream()
                .filter(user -> user.getRole().stream().anyMatch(role -> role.getName().equals("CLIENT")))
                .map(user -> {
                    Map<String, Object> safeUser = new HashMap<>();
                    safeUser.put("username", user.getUsername());
                    safeUser.put("firstName", user.getFirstName());
                    safeUser.put("lastName", user.getLastName());
                    return safeUser;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientsOnly);
    }
}