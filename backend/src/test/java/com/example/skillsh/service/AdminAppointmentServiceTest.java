package com.example.skillsh.service;

import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.dto.appointment.AppointmentAdminDto;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.AppointmentRepo;
import com.example.skillsh.repository.SkillRepo;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.appointment.AdminAppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAppointmentServiceTest {

    @Mock
    private AppointmentRepo appointmentRepository;
    @Mock
    private UserRepo userRepo;
    @Mock
    private SkillRepo skillRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminAppointmentService adminAppointmentService;

    @Test
    void testGetAllAppointments() {
        // 1. Arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setName("Консултация");

        // Мокваме findAll да върне списък с нашия тестов appointment
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        AppointmentAdminDto dto = new AppointmentAdminDto();
        dto.setName("Консултация");

        // Мокваме ModelMapper-a да превърне Entity-то в DTO
        when(modelMapper.map(appointment, AppointmentAdminDto.class)).thenReturn(dto);

        // 2. Act
        List<AppointmentAdminDto> result = adminAppointmentService.getAllAppointments();

        // 3. Assert
        assertEquals(1, result.size());
        assertEquals("Консултация", result.get(0).getName());
        verify(appointmentRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(appointment, AppointmentAdminDto.class);
    }

    @Test
    void testUpdateAppointment_Success() {
        // Arrange
        Long appointmentId = 1L;
        AddAppointment dto = new AddAppointment();
        dto.setName("Обновено име на среща");
        dto.setRequesterUsername("new_requester");

        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);
        existingAppointment.setName("Старо име");

        User newRequester = new User();
        newRequester.setUsername("new_requester");

        // Указваме, че срещата съществува
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        // Указваме, че новият заявител съществува
        when(userRepo.findUserByUsername("new_requester")).thenReturn(Optional.of(newRequester));
        // Мокваме запазването
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // Act
        Appointment result = adminAppointmentService.updateAppointment(appointmentId, dto);

        // Assert
        assertEquals("Обновено име на среща", result.getName());
        assertEquals(newRequester, result.getRequester());
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void testUpdateAppointment_ThrowsExceptionWhenNotFound() {
        // Arrange
        Long appointmentId = 99L;
        AddAppointment dto = new AddAppointment();

        // Указваме, че базата връща празен резултат (Optional.empty)
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminAppointmentService.updateAppointment(appointmentId, dto);
        });

        assertEquals("Записването не е намерено!", exception.getMessage());
        // Уверяваме се, че save() никога не е извикан
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void testCreateAppointment_Success() {
        // 1. Arrange (Подготовка на данните)
        AddAppointment dto = new AddAppointment();
        dto.setName("Тестова среща");
        dto.setRequesterUsername("requester_user");
        dto.setProviderUsername("provider_user");
        dto.setSkillId(1L);

        User requester = new User();
        requester.setUsername("requester_user");

        User provider = new User();
        provider.setUsername("provider_user");

        Skill skill = new Skill();
        skill.setId(1L);

        // Указваме на мокнатите репозиторита какво да върнат
        when(userRepo.findUserByUsername("requester_user")).thenReturn(Optional.of(requester));
        when(userRepo.findUserByUsername("provider_user")).thenReturn(Optional.of(provider));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        Appointment savedAppointment = new Appointment();
        savedAppointment.setName("Тестова среща");
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // 2. Act (Изпълнение на метода)
        Appointment result = adminAppointmentService.createAppointment(dto);

        // 3. Assert (Проверка на резултата)
        assertNotNull(result);
        assertEquals("Тестова среща", result.getName());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testCreateAppointment_ThrowsExceptionWhenRequesterNotFound() {
        // Arrange
        AddAppointment dto = new AddAppointment();
        dto.setRequesterUsername("unknown_user");

        when(userRepo.findUserByUsername("unknown_user")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminAppointmentService.createAppointment(dto);
        });

        assertEquals("Заявителят не е намерен!", exception.getMessage());
        // Проверяваме, че запис в базата НЕ е извикан
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void testDeleteAppointment_Success() {
        // Arrange
        Long appointmentId = 1L;
        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);

        // Act
        adminAppointmentService.deleteAppointment(appointmentId);

        // Assert
        verify(appointmentRepository, times(1)).deleteById(appointmentId);
    }
}


