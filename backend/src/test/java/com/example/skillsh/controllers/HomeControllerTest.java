package com.example.skillsh.controllers;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.dto.user.UserProfileDTO;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.comment.CommentService;
import com.example.skillsh.services.file.FileService;
import com.example.skillsh.services.review.ReviewService;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.services.user.UserServiceImpl; // ПРОМЯНА: Импортираме Impl класа
import com.example.skillsh.web.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ПРОМЯНА: Мокваме UserServiceImpl, защото контролерът очаква точно него!
    @MockBean private UserServiceImpl userService;

    @MockBean private SkillServiceImpl skillService;
    @MockBean private ReviewService reviewService;
    @MockBean private CommentService commentService;
    @MockBean private FileService fileService;

    // Секюрити фикс
    @MockBean private UserDetailsService userDetailsService;

    // 1. Тест за Главната страница
    @Test
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    // 2. Тест за страница Експерти
    @Test
    void testExpertsPage() throws Exception {
        when(userService.users()).thenReturn(List.of(new UserView()));

        mockMvc.perform(get("/experts"))
                .andExpect(status().isOk())
                .andExpect(view().name("experts"))
                .andExpect(model().attributeExists("experts"));
    }

    // 3. Тест за Профилната страница
    @Test
    void testMyProfilePage() throws Exception {
        // Симулираме логнат потребител
        Principal mockPrincipal = () -> "ivan_test";
        when(userService.getUserByUsername("ivan_test")).thenReturn(new UserProfileDTO());

        mockMvc.perform(get("/my-profile").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("userProfile"));
    }

    // 4. Тест за Търсенето
    @Test
    void testSearch() throws Exception {
        when(userService.searchUsers(any(String.class))).thenReturn(List.of(new UserView()));

        mockMvc.perform(post("/search")
                        .param("keyword", "Java")) // Подаваме параметър от формата
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("users"));
    }

    // 5. Тест за paymentMethod ендпойнта (връща JSON)
    @Test
    void testPaymentMethod() throws Exception {
        mockMvc.perform(get("/paymentMethod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ready_for_payment"));
    }

    // 6. Тест за hosted плащане (връща JSON)
    @Test
    void testHostedPayment() throws Exception {
        mockMvc.perform(get("/hosted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("hosted_payment_page"));
    }

    // 7. Тест за Logout (връща JSON)
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}