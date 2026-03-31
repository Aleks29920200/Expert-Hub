package com.example.skillsh.controllers;


import com.example.skillsh.domain.dto.skill.SkillDto;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.web.AdminSkillController;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AdminSkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminSkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillServiceImpl skillService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllSkills_ShouldReturn200AndList() throws Exception {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setName("Java Programming");

        when(skillService.getSkills()).thenReturn(List.of(skillDto));

        mockMvc.perform(get("/api/skills/admin/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Programming"));

        verify(skillService, times(1)).getSkills();
    }

    @Test
    void testCreateSkill_ShouldReturn200AndCreatedSkill() throws Exception {
        SkillDto requestDto = new SkillDto();
        requestDto.setName("Spring Boot");

        SkillDto responseDto = new SkillDto();
        responseDto.setId(2L);
        responseDto.setName("Spring Boot");

        when(skillService.saveSkill(any(SkillDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/skills/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Spring Boot"));

        verify(skillService, times(1)).saveSkill(any(SkillDto.class));
    }

    @Test
    void testUpdateSkill_ShouldReturn200() throws Exception {
        Long skillId = 1L;
        SkillDto requestDto = new SkillDto();
        requestDto.setName("Java Advanced");

        SkillDto responseDto = new SkillDto();
        responseDto.setId(skillId);
        responseDto.setName("Java Advanced");

        when(skillService.updateSkill(eq(skillId), any(SkillDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/skills/admin/update/{id}", skillId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java Advanced"));
    }

    @Test
    void testDeleteSkill_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/skills/admin/delete/{id}", 1L))
                .andExpect(status().isOk());

        verify(skillService, times(1)).deleteSkill(1L);
    }
}


