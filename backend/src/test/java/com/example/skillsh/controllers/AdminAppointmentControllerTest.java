package com.example.skillsh.controllers;


import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.dto.appointment.AppointmentAdminDto;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.services.appointment.AdminAppointmentService;
import com.example.skillsh.web.AdminAppointmentController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Казваме на Spring да зареди САМО този контролер, за да е бърз тестът
@WebMvcTest(AdminAppointmentController.class)
@AutoConfigureMockMvc(addFilters = false) // Изключваме Spring Security защитите за теста
class AdminAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Използва се за превръщане на обекти в JSON

    // Използваме @MockBean вместо @Mock, за да го сложим в Spring контекста
    @MockBean
    private AdminAppointmentService adminAppointmentService;

    @Test
    void testGetAllAppointments_ShouldReturn200AndList() throws Exception {
        // Arrange
        AppointmentAdminDto dto = new AppointmentAdminDto();
        dto.setName("Консултация");
        when(adminAppointmentService.getAllAppointments()).thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/admin/appointments/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Очакваме HTTP статус 200
                .andExpect(jsonPath("$.size()").value(1)) // Очакваме 1 елемент в JSON масива
                .andExpect(jsonPath("$[0].name").value("Консултация"));

        verify(adminAppointmentService, times(1)).getAllAppointments();
    }

    @Test
    void testCreateAppointment_ShouldReturn200AndCreatedAppointment() throws Exception {
        // Arrange
        AddAppointment requestDto = new AddAppointment();
        requestDto.setName("Нова среща");

        Appointment savedAppointment = new Appointment();
        savedAppointment.setId(1L);
        savedAppointment.setName("Нова среща");

        when(adminAppointmentService.createAppointment(any(AddAppointment.class))).thenReturn(savedAppointment);

        // Act & Assert
        mockMvc.perform(post("/api/admin/appointments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Превръщаме requestDto в JSON стринг
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Нова среща"));

        verify(adminAppointmentService, times(1)).createAppointment(any(AddAppointment.class));
    }

    @Test
    void testUpdateAppointment_ShouldReturn200() throws Exception {
        // Arrange
        Long appointmentId = 1L;
        AddAppointment requestDto = new AddAppointment();
        requestDto.setName("Обновена среща");

        Appointment updatedAppointment = new Appointment();
        updatedAppointment.setId(appointmentId);
        updatedAppointment.setName("Обновена среща");

        when(adminAppointmentService.updateAppointment(eq(appointmentId), any(AddAppointment.class)))
                .thenReturn(updatedAppointment);

        // Act & Assert
        mockMvc.perform(put("/api/admin/appointments/update/{id}", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновена среща"));
    }

    @Test
    void testDeleteAppointment_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/appointments/delete/{id}", 1L))
                .andExpect(status().isOk());

        // Проверяваме дали сървисът е извикан правилно
        verify(adminAppointmentService, times(1)).deleteAppointment(1L);
    }
}


