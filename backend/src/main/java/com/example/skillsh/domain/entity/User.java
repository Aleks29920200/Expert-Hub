package com.example.skillsh.domain.entity;

import com.example.skillsh.domain.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User{
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String firstName;
    @JsonIgnore
    private String password;
    private String lastName;
    private String address;
    private String picture;
    @Column(columnDefinition = "TEXT")
    private String bio;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Review> review;
    @OneToMany(fetch = FetchType.EAGER)

    private List<Appointment>appointments;
    @Enumerated(EnumType.STRING)
    private Status activity;
    @ManyToMany(fetch = FetchType.LAZY)

    private Set<Role> role;
    @OneToOne(cascade = CascadeType.MERGE)

    private FileEntity photoUrl;
    @ManyToMany(fetch = FetchType.LAZY)

    private List<User> blockedUsers;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )

    private List<Skill>skills;
}

