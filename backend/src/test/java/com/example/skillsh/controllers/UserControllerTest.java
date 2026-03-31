package com.example.skillsh.controllers;

import com.example.skillsh.domain.entity.Role;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.chat.BlockService;
import com.example.skillsh.services.comment.CommentService;
import com.example.skillsh.services.review.ReviewService;
import com.example.skillsh.services.user.UserService;
import com.example.skillsh.web.UserController;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Мокваме ВСИЧКИ сървиси, които са инжектирани или се използват в UserController
    @MockBean private UserService userService;
    @MockBean private ReviewService reviewService;
    @MockBean private CommentService commentService;
    @MockBean private BlockService blockService;
    @MockBean private ModelMapper modelMapper;

    // Секюрити фикс
    @MockBean private UserDetailsService userDetailsService;

    // --- ТЕСТ 1: Взимане на всички клиенти ---
    @Test
    void testGetAllClients_ShouldReturnOnlyClients() throws Exception {
        // Arrange
        Role clientRole = new Role();
        clientRole.setName("CLIENT");

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User clientUser = new User();
        clientUser.setUsername("client_ivan");
        clientUser.setFirstName("Ivan");
        clientUser.setRole(Set.of(clientRole));

        User adminUser = new User();
        adminUser.setUsername("admin_georgi");
        adminUser.setRole(Set.of(adminRole));

        // Сървисът връща и двамата потребители
        when(userService.getAll()).thenReturn(List.of(clientUser, adminUser));

        // Act & Assert
        // Увери се, че пътят "/api/users/clients" съвпада с твоя @RequestMapping + @GetMapping
        mockMvc.perform(get("/api/users/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1)) // Трябва да е 1, защото админът е филтриран!
                .andExpect(jsonPath("$[0].username").value("client_ivan"))
                .andExpect(jsonPath("$[0].firstName").value("Ivan"));
    }

    // --- ТЕСТ 2: Взимане на съществуващ профил (200 OK) ---
    @Test
    void testGetUserProfile_WhenUserExists_ShouldReturn200() throws Exception {
        // Този тест предполага, че имаш метод за взимане на профил по username/id,
        // който връща Optional<UserView> и сетва ревютата, както се вижда в кода ти.

        // Arrange
        UserView mockUserView = new UserView();
        mockUserView.setUsername("test_user");

      //  when(userService.getUserByUsername("test_user")).thenReturn(Optional.of(mockUserView));


        mockMvc.perform(get("/api/users/profile/test_user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userProfile.username").value("test_user"));

    }

    // --- ТЕСТ 3: Взимане на несъществуващ профил (404 Not Found) ---
    @Test
    void testGetUserProfile_WhenUserDoesNotExist_ShouldReturn404() throws Exception {

       //  when(userService.getUserByUsername("unknown_user")).thenReturn();


        mockMvc.perform(get("/api/users/profile/unknown_user"))
                .andExpect(status().isNotFound());

    }
}


