package com.example.skillsh.domain.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String password;
    private String lastName;
    private String address;
    private String picture;

}
