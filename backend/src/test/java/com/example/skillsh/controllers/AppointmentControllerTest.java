package com.example.skillsh.controllers;

import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.services.appointment.AppointmentService;
import com.example.skillsh.services.skill.SkillService;
import com.example.skillsh.services.user.UserService;
import com.example.skillsh.web.AppointmentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private UserService userService;

    @MockBean
    private SkillService skillService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllAppointments_ShouldReturnMappedList() throws Exception {
        // Arrange
        User requester = new User();
        requester.setUsername("ivan_req");

        User provider = new User();
        provider.setUsername("petar_prov");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setName("Консултация");
        appointment.setRequester(requester);
        appointment.setProvider(provider);

        when(appointmentService.getAllAppointments()).thenReturn(List.of(appointment));

        // Act & Assert
        mockMvc.perform(get("/api/appointments/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Консултация"))
                .andExpect(jsonPath("$[0].requesterUsername").value("ivan_req"))
                .andExpect(jsonPath("$[0].providerUsername").value("petar_prov"));

        verify(appointmentService, times(1)).getAllAppointments();
    }

    @Test
    void testGetCalendar_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/appointments/calendar"))
                .andExpect(status().isOk());
    }
}


