package com.example.skillsh.web;




import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.dto.skill.SkillDto;
import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.services.appointment.AdminAppointmentService;
import com.example.skillsh.services.skill.SkillService;
import com.example.skillsh.services.skill.SkillServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills/admin")
public class AdminSkillController {

    private final SkillServiceImpl skillService;

    public AdminSkillController(SkillServiceImpl skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        return ResponseEntity.ok(skillService.getSkills());
    }

    @PostMapping("/create")
    public ResponseEntity<SkillDto> createSkill(@RequestBody SkillDto skillDTO) {
        return ResponseEntity.ok(skillService.saveSkill(skillDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SkillDto> updateSkill(@PathVariable Long id, @RequestBody SkillDto skillDTO) {
        return ResponseEntity.ok(skillService.updateSkill(id, skillDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}


