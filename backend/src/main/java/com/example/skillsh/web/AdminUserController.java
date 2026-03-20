package com.example.skillsh.web;

import com.example.skillsh.domain.dto.user.UserDTO;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.user.UserService;
import com.example.skillsh.services.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private UserServiceImpl userService; // Твоят сървис за работа с базата

    // READ - Вземане на всички
    @GetMapping("/all")
    public ResponseEntity<List<UserView>> getAllUsers() {
        return ResponseEntity.ok(userService.users());
    }

    // CREATE - Нов потребител
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.saveUser(userDTO));
    }

    // UPDATE - Редакция
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser2(id, userDTO));
    }

    // DELETE - Изтриване
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
