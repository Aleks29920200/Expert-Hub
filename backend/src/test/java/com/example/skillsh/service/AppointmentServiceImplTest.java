package com.example.skillsh.service;


import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.AppointmentRepo;
import com.example.skillsh.services.appointment.AppointmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void testAddAppointment() {
        // Arrange
        AddAppointment dto = new AddAppointment();
        dto.setName("Урок по Java");

        User requester = new User();
        User provider = new User();
        Skill skill = new Skill();

        // Използваме ArgumentCaptor, за да уловим обекта, който се подава на save()
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);

        // Act
        appointmentService.addAppointment(dto, requester, provider, skill);

        // Assert
        verify(appointmentRepo, times(1)).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();

        assertEquals("Урок по Java", savedAppointment.getName());
        assertEquals(requester, savedAppointment.getRequester());
        assertEquals(provider, savedAppointment.getProvider());
        assertEquals(skill, savedAppointment.getSkill());
    }

    @Test
    void testRemoveAppointment() {
        // Arrange
        Long idToRemove = 5L;

        // Act
        appointmentService.removeAppointment(idToRemove);

        // Assert
        verify(appointmentRepo, times(1)).deleteById(idToRemove);
    }
    @Test
    void testGetAllAppointments() {
        // Arrange
        Appointment app1 = new Appointment();
        app1.setId(1L);

        Appointment app2 = new Appointment();
        app2.setId(2L);

        // Връщаме списък с две срещи
        when(appointmentRepo.findAll()).thenReturn(List.of(app1, app2));

        // Act
        List<Appointment> result = appointmentService.getAllAppointments();

        // Assert
        assertEquals(2, result.size());
        verify(appointmentRepo, times(1)).findAll();
    }
}

