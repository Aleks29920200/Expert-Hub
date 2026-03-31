package com.example.skillsh.service;

import com.example.skillsh.domain.dto.skill.AddSkillDTO;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.enums.CategoryType;
import com.example.skillsh.repository.SkillRepo;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.services.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.skillsh.domain.entity.enums.CategoryType.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private SkillRepo skillRepo;

    @Mock
    private UserServiceImpl userService; // Мокваме го, защото го има в конструктора

    @InjectMocks
    private SkillServiceImpl skillService;

    @Test
    void testSeedSkills_ShouldSaveMissingCategories() {
        // Arrange
        // 1. По подразбиране казваме на Mockito: "За която и да е категория, върни съществуващ обект"
        // lenient() премахва строгите проверки на Mockito, които хвърлят твоята грешка.
        lenient().when(skillRepo.getSkillByCategory(anyString())).thenReturn(new Skill());

        // 2. НО специално за "transportation" симулираме, че ЛИПСВА в базата (връщаме null)
        lenient().when(skillRepo.getSkillByCategory("transportation")).thenReturn(null);

        // Act
        skillService.seedCategories(); // (Ако методът ти се казва seedCategories, промени го тук)

        // Assert
        ArgumentCaptor<Skill> skillCaptor = ArgumentCaptor.forClass(Skill.class);

        // Тъй като само "transportation" липсва, очакваме save() да е извикан точно 1 път
        verify(skillRepo, times(1)).save(skillCaptor.capture());

        assertEquals("transportation", skillCaptor.getValue().getCategory());
    }

    @Test
    void testAddSkill() {
        // Arrange
        AddSkillDTO dto = new AddSkillDTO();
        dto.setName("Java Spring Boot");
        dto.setTag("Backend");
        dto.setCategory(valueOf("IT"));
        dto.setDescription("Опит в изграждане на уеб приложения.");

        // Act
        skillService.addSkill(dto);

        // Assert
        ArgumentCaptor<Skill> skillCaptor = ArgumentCaptor.forClass(Skill.class);
        verify(skillRepo, times(1)).save(skillCaptor.capture());

        Skill savedSkill = skillCaptor.getValue();
        assertEquals("Java Spring Boot", savedSkill.getName());
        assertEquals("Backend", savedSkill.getTag());
        assertEquals("IT", savedSkill.getCategory());
        assertEquals("Опит в изграждане на уеб приложения.", savedSkill.getDescription());
    }

    @Test
    void testUpdateSkill_Success() {
        // Arrange
        Skill updatedSkill = new Skill();
        updatedSkill.setId(1L);
        updatedSkill.setName("Ново Име");
        updatedSkill.setCategory("Нова Категория");

        Skill existingSkill = new Skill();
        existingSkill.setId(1L);
        existingSkill.setName("Старо Име");
        existingSkill.setCategory("Стара Категория");

        when(skillRepo.findById(1L)).thenReturn(Optional.of(existingSkill));

        // Act
        skillService.updateSkill(updatedSkill);

        // Assert
        assertEquals("Ново Име", existingSkill.getName());
        assertEquals("Нова Категория", existingSkill.getCategory());
        verify(skillRepo, times(1)).save(existingSkill);
    }

    @Test
    void testUpdateSkill_ThrowsExceptionWhenNotFound() {
        // Arrange
        Skill updatedSkill = new Skill();
        updatedSkill.setId(99L); // Несъществуващо ID

        when(skillRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            skillService.updateSkill(updatedSkill);
        });

        assertEquals("Invalid skill ID: 99", exception.getMessage());
        verify(skillRepo, never()).save(any());
    }

    @Test
    void testDeleteSkill() {
        // Arrange
        Long skillId = 1L;

        // Act
        skillService.deleteSkill(skillId);

        // Assert
        verify(skillRepo, times(1)).deleteById(skillId);
    }
}
