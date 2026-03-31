package com.example.skillsh.domain.dto.appointment;

import com.example.skillsh.domain.entity.enums.StatusName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class AppointmentAdminDto {
    private Long id;
    private String name;
    // Отново: ModelMapper ще ги мапне автоматично от requester.username и provider.username
    private String requesterUsername;
    private String providerUsername;
    private String skillName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateOfAppointment;
    private StatusName status;
}
