package com.example.skillsh.domain.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class UserProfileDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String photoUrl; // Changed to String to hold the base64/URL
    private Set<String> role;
}

