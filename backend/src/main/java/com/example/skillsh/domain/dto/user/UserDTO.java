package com.example.skillsh.domain.dto.user;

import com.example.skillsh.domain.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String password;
    private String lastName;
    private String address;
    private String picture;
    private List<Role> roles;

}
