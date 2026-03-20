package com.example.skillsh.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name="skills")
public class Skill {
    @Id
    @Column(name="skill_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String category;
    private String tag;
    @ManyToMany(mappedBy = "skills") // Refers to the field name in User
    private List<User> users = new ArrayList<>();
    public Skill() {

    }

}
