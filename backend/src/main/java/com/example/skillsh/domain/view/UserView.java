package com.example.skillsh.domain.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserView {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean hasPaid;
    private byte[] photoUrl; // (или String, зависи как си го дефинирал в User.java)
    private String bio;
    // Ако искаш да показваш и ревютата:
    private List<ReviewDTO> reviews;
}
 // Смени пакета, ако твоят е различен

