package com.example.skillsh.web;

import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.services.appointment.AppointmentService;
import com.example.skillsh.services.skill.SkillService;
import com.example.skillsh.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController // Changed from @Controller
@RequestMapping("/api/appointments") // Good practice to group under /api
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") // Allow Angular
public class AppointmentController {

    private final AppointmentService appointmentService;
    private UserService userService;
    private SkillService skillService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, UserService userService, SkillService skillService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.skillService = skillService;
    }

    // Angular doesn't need a GET to "show" the add form. It handles the form UI itself.
    // However, if you need to fetch available slots/providers for the dropdowns, add that here.

    @PostMapping("/add")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<?> addAppointment(@Valid @RequestBody AddAppointment addAppointment,
                                            BindingResult bindingResult) { // <-- МАХНАХМЕ Principal principal оттук

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            // ВЗИМАМЕ ЕКСПЕРТА ОТ DTO-ТО, А НЕ ОТ PRINCIPAL!
            User provider = userService.findUserByUsername(addAppointment.getProviderUsername())
                    .orElseThrow(() -> new RuntimeException("Експертът не е намерен"));

            User requester = userService.findUserByUsername(addAppointment.getRequesterUsername())
                    .orElseThrow(() -> new RuntimeException("Клиентът не е намерен"));

            Skill skill = skillService.getSkillById(addAppointment.getSkillId());

            appointmentService.addAppointment(addAppointment, requester, provider, skill);

            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment created successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<?> removeAppointment(@PathVariable Long id) {
        appointmentService.removeAppointment(id); // Трябва да си оправил и Service метода, както говорихме миналия път
        return ResponseEntity.ok("Appointment removed successfully");
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('EXPERT')")
    @Transactional // Държи връзката отворена
    public ResponseEntity<List<Map<String, Object>>> getAllAppointments() {

        List<Appointment> appointments = appointmentService.getAllAppointments();

        // Превръщаме сложните обекти (Appointment) в прости речници (Map),
        // за да не се опитва Spring да зарежда ролите на потребителите
        List<Map<String, Object>> safeAppointments = appointments.stream().map(app -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("name", app.getName());
            map.put("dateOfAppointment", app.getDateOfAppointment());

            // Взимаме само името (String), а не целия обект User
            if (app.getRequester() != null) {
                map.put("requesterUsername", app.getRequester().getUsername());
            }
            if (app.getProvider() != null) {
                map.put("providerUsername", app.getProvider().getUsername());
            }

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(safeAppointments);
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<List<?>> getCalendar() {
        // You need to update your Service to return a List of appointments
        // List<AppointmentDTO> appointments = appointmentService.findAllForUser();
        // return ResponseEntity.ok(appointments);

        // Placeholder until service is updated:
        return ResponseEntity.ok().build();
    }
}
