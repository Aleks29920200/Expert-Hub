package com.example.skillsh.web;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.dto.user.RegisterAsClientDto;
import com.example.skillsh.domain.dto.user.RegisterAsExpertDto;
import com.example.skillsh.domain.dto.user.UserProfileDTO;
import com.example.skillsh.domain.entity.FileEntity;
import com.example.skillsh.domain.entity.Role;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.domain.entity.enums.Status;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.comment.CommentService;
import com.example.skillsh.services.file.FileService;
import com.example.skillsh.services.review.ReviewService;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.services.user.UserService;
import com.example.skillsh.services.user.UserServiceImpl;
import com.example.skillsh.util.ImageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
// Crucial for your Angular app: allowCredentials allows cookies/headers to pass through
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class HomeController {

    private final UserServiceImpl userService;
    private final SkillServiceImpl skillService;
    private final AppointmentController appointmentController;
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final FileService fileService;

    @Autowired
    public HomeController(UserServiceImpl userService, SkillServiceImpl skillService, AppointmentController appointmentController, ReviewService reviewService, CommentService commentService, FileService fileService) {
        this.userService = userService;
        this.skillService = skillService;
        this.appointmentController = appointmentController;
        this.reviewService = reviewService;
        this.commentService = commentService;
        this.fileService = fileService;
    }

    // --- GET Methods converted to return JSON ---

    @GetMapping("/index")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<List<UserView>> index() {
        // Instead of returning a view "index", we return the data intended for it
        return ResponseEntity.ok(userService.users());
    }

    @GetMapping("/home/search/{category}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<UserView>> categories(@PathVariable(required = false, name = "category") String category) {
        Skill skillByCategory = skillService.findUserBySkill(category);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<UserView> users = userService.findAllBySkills(Collections.singletonList(skillByCategory.getId()))
                .stream()
                .filter(e -> !Objects.equals(e.getUsername(), authentication.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/home")
    @PreAuthorize("hasAnyAuthority('ADMIN','CLIENT')")
    public ResponseEntity<Map<String, Object>> homePage(Principal principal, @AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> response = new HashMap<>();

        if (oauth2User != null) {
            String email = oauth2User.getAttribute("email");
            if (userService.findUserByEmail(email).isEmpty()) {
                userService.registerO2AuthUser(oauth2User);
            }
            response.put("user", userService.findUserByEmail(email).orElse(null));
            return ResponseEntity.ok(response);
        }

        User currentUser = userService.findUserByUsername(principal.getName()).orElse(null);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Role admin = currentUser.getRole().stream()
                .filter(e -> e.getName().contains("ADMIN"))
                .findFirst().orElse(null);

        if (currentUser.getActivity().equals(Status.ONLINE)) {
            currentUser.setActivity(Status.OFFLINE);
            // Ideally save the user state change here -> userService.save(currentUser);
        }

        response.put("currentUser", currentUser);
        response.put("isAdmin", admin != null);

        if (admin == null) {
            List<User> skills = userService.getAll().stream()
                    .filter(e -> !e.getSkills().isEmpty() && !Objects.equals(e.getUsername(), principal.getName()))
                    .toList();
            response.put("skills", skills);
        }

        List<User> usersWithReviews = userService.getUsersWithReviews();
        response.put("usersWithReviews", usersWithReviews);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable("username") String username) {
        UserProfileDTO userProfile = userService.getUserByUsername(username);

        if (userProfile==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userProfile", userProfile);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName());

        response.put("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String currentUsername = authentication.getName();
            userService.findUserByUsername(currentUsername)
                    .ifPresent(currentUser -> response.put("currentUserId", currentUser.getId()));
        }

        return ResponseEntity.ok(response);
    }

    // --- Registration Endpoints (Already good, just slight clean up) ---

    @PostMapping("/register/expert")
    public ResponseEntity<?> registerExpert(@Valid @ModelAttribute RegisterAsExpertDto registerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.registerUserAsExpert(registerDto);
        return ResponseEntity.ok(Map.of("message", "Expert registered successfully"));
    }

    // ✅ Client was already correct
    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@Valid @ModelAttribute RegisterAsClientDto registerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.registerUserAsClient(registerDto);
        return ResponseEntity.ok(Map.of("message", "Client registered successfully"));
    }

    // --- Simple State/Nav Endpoints ---

    @GetMapping("/home/pay")
    public ResponseEntity<Map<String, String>> paymentMethod() {
        return ResponseEntity.ok(Map.of("status", "ready_for_payment"));
    }

    @GetMapping("/hosted")
    public ResponseEntity<Map<String, String>> pay() {
        return ResponseEntity.ok(Map.of("status", "hosted_payment_page"));
    }





    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout() {
        // Spring Security usually handles the actual session invalidation.
        // This just returns a success response to the frontend.
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // --- Deprecated / Helper Methods ---
    // Note: In REST, @ModelAttribute methods that returned empty DTOs for Thymeleaf forms
    // are usually not needed because the frontend (Angular) creates the JSON object.
    // I have kept them here but they won't automatically be included in JSON responses
    // unless you explicitly add them to the returned Map in the methods above.

    /*
    @ModelAttribute("skill")
    public Skill skill() { return new Skill(); }

    @ModelAttribute(name="file")
    public FileEntity file() { return new FileEntity(); }

    @ModelAttribute(name="registerAsExpertDto")
    public RegisterAsExpertDto registerAsExpertDtoDto() { return new RegisterAsExpertDto(); }

    @ModelAttribute(name="registerAsClientDto")
    public RegisterAsClientDto registerAsClientDtoDto() { return new RegisterAsClientDto(); }
    */
}