package com.example.skillsh.service;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.services.search.SearchServiceImpl;
import com.example.skillsh.services.skill.SkillServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    // Мокваме SkillServiceImpl, тъй като той се ползва в SearchServiceImpl
    @Mock
    private SkillServiceImpl skillService;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void testFindSearchedInformationByCategory() {
        // Arrange
        SearchDto searchDto = new SearchDto();
        searchDto.setInfo("Java Programming");

        Skill expectedSkill = new Skill();
        expectedSkill.setName("Java Programming");
        expectedSkill.setCategory("IT");

        // Когато searchService извика getSkillByName с "Java Programming", връщаме мокнатия Skill
        when(skillService.getSkillByName("Java Programming")).thenReturn(expectedSkill);

        // Act
        Skill result = searchService.findSearchedInformationByCategory(searchDto);

        // Assert
        assertNotNull(result);
        assertEquals("Java Programming", result.getName());
        assertEquals("IT", result.getCategory());

        // Проверяваме дали методът на SkillServiceImpl е извикан точно 1 път с правилния параметър
        verify(skillService, times(1)).getSkillByName("Java Programming");
    }
}
