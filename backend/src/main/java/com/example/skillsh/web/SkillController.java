package com.example.skillsh.web;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.dto.skill.AddSkillDTO;
import com.example.skillsh.domain.dto.skill.SkillDto;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.view.SkillView;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.util.ImageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/skills")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SkillController {

    private final SkillServiceImpl skillService;

    @Autowired
    public SkillController(SkillServiceImpl skillService) {
        this.skillService = skillService;
    }



    @GetMapping("/{id}")
    public ResponseEntity<SkillView> getSkill(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkill(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addSkill(@Valid @RequestBody AddSkillDTO addSkillDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        skillService.addSkill(addSkillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Skill added successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        skillService.updateSkill(skill);
        return ResponseEntity.ok("Skill updated successfully");
    }
    @GetMapping("/all")
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        List<SkillDto> skills = skillService.getSkills(); // Увери се, че имаш такъв метод в SkillService
        return ResponseEntity.ok(skills);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok("Skill deleted successfully");
    }
    @ModelAttribute("user")
    public UserView user(){
        return new UserView();
    }
    @ModelAttribute("skill")
    public Skill skill(){
        return new Skill();
    }
    @ModelAttribute("searchDto")
    public SearchDto searchDto(){
        return new SearchDto();
    }
    @ModelAttribute("imageUtil")
    public ImageUtil image(){
        return new ImageUtil();
    }
}