package com.example.skillsh.domain.dto.skill;

import com.example.skillsh.domain.entity.enums.CategoryType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddSkillDTO {
    @Size(max = 50)
    private String name;

    private String description;

    private CategoryType category;

    @Size(max = 60)
    private String tag;
}