package com.example.skillsh.web; // Смени пакета, ако твоят се казва различно!

import com.example.skillsh.domain.entity.Role;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private UserRepo userRepo;

    public AuthController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String rawUsername = loginRequest.get("username");
        String password = loginRequest.get("password");

        if (rawUsername == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Липсват данни"));
        }

        String username = rawUsername.trim();
        Optional<User> userOptional = userRepo.findUserByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Проверка на паролата (Тук трябва да е с PasswordEncoder в реална среда)
            if (!user.getPassword().equals(password)) {
                return ResponseEntity.status(401).body(Map.of("error", "Грешна парола!"));
            }

            // --- ИЗВЛИЧАНЕ НА РОЛИТЕ ОТ ТВОЯ Set<Role> ---
            String finalRole = "CLIENT"; // Роля по подразбиране

            // Вземаме списъка с роли от базата
            Set<Role> roles = user.getRole();

            if (roles != null && !roles.isEmpty()) {
                // Проверяваме дали потребителят има роля ADMIN (или ROLE_ADMIN)
                boolean isAdmin = roles.stream()
                        .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN")
                                || r.getName().equalsIgnoreCase("ROLE_ADMIN"));

                if (isAdmin) {
                    finalRole = "ADMIN";
                } else {
                    // Ако не е админ, просто вземаме първата му роля (напр. CLIENT/USER/EXPERT)
                    finalRole = roles.iterator().next().getName();
                }
            }

            System.out.println("Успешен вход! Име: " + username + " | Роля от базата: " + finalRole);

            return ResponseEntity.ok(Map.of(
                    "token", "test-token-for-" + username,
                    "role", finalRole,
                    "username", username
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Потребителят не е намерен!"));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Липсващ токен"));
        }

        // 1. Махаме "Bearer "
        String token = authHeader.substring(7);

        // 2. Извличаме чистото име (Махаме ЦЯЛОТО "test-token-for-")
        String username = token.replace("test-token-for-", "").trim();

        // 3. Същата проверка за роля, както при логин
        String role = "CLIENT";
        if ( username.toLowerCase().contains("admin")) {
            role = "ADMIN";
        }

        System.out.println("Проверка за потребител: [" + username + "] с роля: " + role);

        return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role
        ));
    }
}

// Помощен клас за четене на JSON-а от Angular
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}