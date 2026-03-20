package com.example.skillsh.domain.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAppointment {
    private String name;
    private LocalDateTime dateOfAppointment;
    private String requesterUsername;
    private String providerUsername;// Потребителското име на клиента
    private Long skillId;
}

