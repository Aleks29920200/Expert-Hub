package com.example.skillsh.controllers;

import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.skill.SkillService;
import com.example.skillsh.services.user.UserServiceImpl;
import com.example.skillsh.web.SearchController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private SkillService skillService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testSearchUsers_WithEmptyQuery_ShouldReturnAllUsers() throws Exception {
        // Arrange
        UserView user = new UserView();
        user.setUsername("test_user");
        when(userService.users()).thenReturn(List.of(user));

        // Act & Assert
        // Не подаваме параметър "query", затова ще приеме default = ""
        mockMvc.perform(get("/api/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("test_user"));

        verify(userService, times(1)).users();
        verify(userService, never()).searchUsers(anyString());
    }

    @Test
    void testSearchUsers_WithKeyword_ShouldReturnFilteredUsers() throws Exception {
        // Arrange
        UserView user = new UserView();
        user.setUsername("java_expert");
        when(userService.searchUsers("Java")).thenReturn(List.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/search").param("query", " Java ")) // Слагам спейсове, за да тестваме и .trim()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("java_expert"));

        verify(userService, times(1)).searchUsers("Java");
    }

    @Test
    void testSearchByCategory_SkillFound() throws Exception {
        // Arrange
        Skill skill = new Skill();
        skill.setId(5L);
        skill.setCategory("IT");

        UserView user = new UserView();
        user.setUsername("it_guy");

        when(skillService.getSkillByCategory("IT")).thenReturn(skill);
        when(userService.findAllBySkills(Collections.singletonList(5L))).thenReturn(List.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/search/category/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("it_guy"));
    }

    @Test
    void testSearchByCategory_SkillNotFound_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(skillService.getSkillByCategory("Unknown")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/search/category/Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // Проверяваме дали връща празен масив []
    }
}
