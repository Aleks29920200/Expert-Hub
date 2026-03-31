package com.example.skillsh.controllers;

import com.example.skillsh.domain.dto.user.UserDTO;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.user.UserServiceImpl;
import com.example.skillsh.web.AdminUserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService; // <-- ДОБАВЕН ИМПОРТ ЗА SECURITY
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

// <-- ДОБАВЕНИ ИМПОРТИ ЗА ANY() И EQ()
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;


    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllUsers_ShouldReturn200AndList() throws Exception {
        UserView userView = new UserView();
        userView.setUsername("admin_user");
        userView.setEmail("admin@test.com");

        when(userService.users()).thenReturn(List.of(userView));

        mockMvc.perform(get("/api/admin/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("admin_user"));

        verify(userService, times(1)).users();
    }

    @Test
    void testCreateUser_ShouldReturn200AndCreatedUser() throws Exception {
        UserDTO requestDto = new UserDTO();
        requestDto.setUsername("new_user");

        UserDTO responseDto = new UserDTO();
        responseDto.setId(1L);
        responseDto.setUsername("new_user");

        when(userService.saveUser(any(UserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("new_user"));
    }

    @Test
    void testUpdateUser_ShouldReturn200() throws Exception {
        Long userId = 1L;
        UserDTO requestDto = new UserDTO();
        requestDto.setUsername("updated_user");

        UserDTO responseDto = new UserDTO();
        responseDto.setId(userId);
        responseDto.setUsername("updated_user");

        when(userService.updateUser2(eq(userId), any(UserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/admin/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_user"));
    }

    @Test
    void testDeleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/admin/delete/{id}", 1L))
                // ТУК Е ПРОМЯНАТА: Използваме isNoContent() вместо isOk()
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}