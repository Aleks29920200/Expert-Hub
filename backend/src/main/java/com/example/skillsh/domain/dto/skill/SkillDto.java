package com.example.skillsh.domain.dto.skill;

import com.example.skillsh.domain.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    private Long id;
    private String name;
    private String category;
    private List<UserDTO> createdBy;
}