package com.example.skillsh.services.appointment;


import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AppointmentService{





    // ПРОМЯНА ЗА СЪЗДАВАНЕТО
    void addAppointment(AddAppointment addAppointment, User requester, User provider, Skill skill);

    // ПРОМЯНА ЗА ИЗТРИВАНЕТО (Прави се по ID)
    void removeAppointment(Long appointmentId);

    List<Appointment> getAllAppointments();
}

