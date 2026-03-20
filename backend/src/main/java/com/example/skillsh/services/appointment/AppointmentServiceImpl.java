package com.example.skillsh.services.appointment;

import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.entity.*;
import com.example.skillsh.repository.AppointmentRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepo appointmentRepo;
    private ModelMapper mapper=new ModelMapper();
@Autowired
    public AppointmentServiceImpl(AppointmentRepo appointmentRepo, ModelMapper mapper) {
        this.appointmentRepo = appointmentRepo;
        this.mapper = mapper;
}

    @Override
    public void addAppointment(AddAppointment addAppointment, User requester, User provider, Skill skill) {
        Appointment appointment = new Appointment();
        appointment.setName(addAppointment.getName());
        appointment.setDateOfAppointment(addAppointment.getDateOfAppointment());
        // ТРЯБВА ДА ДОБАВИШ ТЕЗИ:
        appointment.setRequester(requester);
        appointment.setProvider(provider);
        appointment.setSkill(skill);
        appointmentRepo.save(appointment);
    }
@Override
public void removeAppointment(Long appointmentId) {
        appointmentRepo.deleteById(appointmentId);
    }



    @Override
    public List<Appointment> getAllAppointments() {
        return this.appointmentRepo.findAll();
    }
}


