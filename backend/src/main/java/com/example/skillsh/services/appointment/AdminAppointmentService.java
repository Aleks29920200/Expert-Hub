package com.example.skillsh.services.appointment;

import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.dto.appointment.AppointmentAdminDto;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.domain.entity.enums.StatusName;
import com.example.skillsh.repository.AppointmentRepo;

import com.example.skillsh.repository.SkillRepo;

import com.example.skillsh.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAppointmentService {

    private final AppointmentRepo appointmentRepository;
    private final UserRepo userRepo;
    private final SkillRepo skillRepository;
    private final ModelMapper modelMapper;

    public AdminAppointmentService(AppointmentRepo appointmentRepository, UserRepo userRepo, SkillRepo skillRepository, ModelMapper modelMapper) {
        this.appointmentRepository = appointmentRepository;
        this.userRepo = userRepo;
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    // 1. READ ALL
    public List<AppointmentAdminDto> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();

        // Превръщаме всяко Appointment (Entity) в AppointmentDTO
        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentAdminDto.class))
                .collect(Collectors.toList());
    }

    // 2. CREATE
    @Transactional
    public Appointment createAppointment(AddAppointment dto) {
        Appointment appointment = new Appointment();

        mapDtoToEntity(dto, appointment);
        appointment.setStatus(StatusName.SCHEDULED); // Задаваме статус по подразбиране

        return appointmentRepository.save(appointment);
    }

    // 3. UPDATE
    @Transactional
    public Appointment updateAppointment(Long id, AddAppointment dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Записването не е намерено!"));

        mapDtoToEntity(dto, appointment);

        return appointmentRepository.save(appointment);
    }

    // 4. DELETE
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Записването не съществува!");
        }
        appointmentRepository.deleteById(id);
    }

    // Помощен метод за мапване
    private void mapDtoToEntity(AddAppointment dto, Appointment appointment) {
        appointment.setName(dto.getName());
        appointment.setDateOfAppointment(dto.getDateOfAppointment());

        // Намираме Requester (Заявител)
        if (dto.getRequesterUsername() != null) {
            User requester = userRepo.findUserByUsername(dto.getRequesterUsername())
                    .orElseThrow(() -> new RuntimeException("Заявителят не е намерен!"));
            appointment.setRequester(requester);
        }

        // Намираме Provider (Доставчик/Експерт)
        if (dto.getProviderUsername() != null) {
            User provider = userRepo.findUserByUsername(dto.getProviderUsername())
                    .orElseThrow(() -> new RuntimeException("Експертът не е намерен!"));
            appointment.setProvider(provider);
        }

        // Намираме Умението (Skill)
        if (dto.getSkillId() != null) {
            Skill skill = skillRepository.findById(dto.getSkillId())
                    .orElseThrow(() -> new RuntimeException("Умението не е намерено!"));
            appointment.setSkill(skill);
        }
    }
}
