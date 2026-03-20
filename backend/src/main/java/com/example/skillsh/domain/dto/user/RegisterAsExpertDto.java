package com.example.skillsh.domain.dto.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAsExpertDto {
    @Length(min=5,max=25)
    private String username;
    @Length(min=5,max=25)
    private String firstName;
    @Length(min=5,max=25)
    private String lastName;
    @Email
    private String email;
    @Length(min=5)
    private String password;
    private String categoryOfSkill;
    private MultipartFile photoUrl;
}
