package com.example.skillsh.domain.entity;

import com.example.skillsh.domain.entity.enums.StatusName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="appointments")
@NoArgsConstructor
public class Appointment {
    @Id
    @Column(name = "appointment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne // ПРОМЕНЕНО
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne // ПРОМЕНЕНО
    @JoinColumn(name = "provider_id")
    private User provider;

    @ManyToOne // ПРОМЕНЕНО
    @JoinColumn(name = "skill_id")
    private Skill skill;
    private LocalDateTime dateOfAppointment;
    private String name;
    @Enumerated(EnumType.STRING)
    private StatusName status;
}






