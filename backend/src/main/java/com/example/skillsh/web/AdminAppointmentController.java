package com.example.skillsh.web;

import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.dto.appointment.AppointmentAdminDto;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.services.appointment.AdminAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminAppointmentController {

    private final AdminAppointmentService adminAppointmentService;

    public AdminAppointmentController(AdminAppointmentService adminAppointmentService) {
        this.adminAppointmentService = adminAppointmentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppointmentAdminDto>> getAllAppointments() {
        return ResponseEntity.ok(adminAppointmentService.getAllAppointments());
    }

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(@RequestBody AddAppointment dto) {
        return ResponseEntity.ok(adminAppointmentService.createAppointment(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody AddAppointment dto) {
        return ResponseEntity.ok(adminAppointmentService.updateAppointment(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        adminAppointmentService.deleteAppointment(id);
        return ResponseEntity.ok().build();
    }
}





