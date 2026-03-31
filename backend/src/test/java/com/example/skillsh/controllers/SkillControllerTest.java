package com.example.skillsh.controllers;

import com.example.skillsh.domain.dto.skill.AddSkillDTO;
import com.example.skillsh.domain.dto.skill.SkillDto;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.enums.CategoryType;
import com.example.skillsh.domain.view.SkillView;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.web.SkillController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillServiceImpl skillService;

    // Секюрити фикс
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetSkill_ShouldReturn200() throws Exception {
        // Arrange
        SkillView skillView = new SkillView();
        skillView.setName("Java");
        when(skillService.getSkill(1L)).thenReturn(skillView);

        // Act & Assert
        mockMvc.perform(get("/api/admin/skills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void testAddSkill_Success_ShouldReturn201() throws Exception {
        // Arrange
        AddSkillDTO dto = new AddSkillDTO();
        dto.setName("Spring Boot"); // Правилно попълнено DTO
        dto.setCategory(CategoryType.valueOf("IT"));

        // Act & Assert
        mockMvc.perform(post("/api/admin/skills/add") // В случай че пътят е само / (без /add), промени го според контролера ти
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Според кода ти, връща HttpStatus.CREATED (201) и стринг
                .andExpect(status().isCreated())
                .andExpect(content().string("Skill added successfully"));

        verify(skillService, times(1)).addSkill(any(AddSkillDTO.class));
    }

    // Тест за обновяване
    @Test
    void testUpdateSkill_ShouldReturn200() throws Exception {
        // Arrange
        Skill skill = new Skill();
        skill.setName("Java Advanced");

        // Act & Assert
        mockMvc.perform(put("/api/admin/skills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk())
                .andExpect(content().string("Skill updated successfully"));

        verify(skillService, times(1)).updateSkill(any(Skill.class));
    }

    @Test
    void testGetAllSkills_ShouldReturn200AndList() throws Exception {
        // Arrange
        SkillDto skillDto = new SkillDto();
        skillDto.setName("Java");
        when(skillService.getSkills()).thenReturn(List.of(skillDto));

        // Act & Assert
        mockMvc.perform(get("/api/admin/skills/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    void testDeleteSkill_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/skills/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Skill deleted successfully"));

        verify(skillService, times(1)).deleteSkill(1L);
    }
}