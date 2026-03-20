package com.example.skillsh.services.skill;

import com.example.skillsh.domain.dto.skill.AddSkillDTO;
import com.example.skillsh.domain.dto.skill.SkillDto;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.domain.view.SkillView;
import com.example.skillsh.repository.SkillRepo;
import com.example.skillsh.services.user.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {
    private SkillRepo skillRepo;
    private ModelMapper mapper=new ModelMapper();
    private UserServiceImpl userService;
    List<User>users = new ArrayList<>();
@Autowired
    public SkillServiceImpl(SkillRepo skillRepo, UserServiceImpl userService) {
        this.skillRepo = skillRepo;
    this.userService = userService;
}
    public List<Skill> skills(){
        return skillRepo.findAll();
    }
    public List<SkillDto> getSkills(){
        return skills().stream().map(e->mapper.map(e,SkillDto .class)).toList();
    }
    public Skill findUserBySkill(String nameOfSkill){
        Skill skill = getSkillByCategory(nameOfSkill);
        return skill;
    }
    public SkillDto saveSkill(SkillDto skillDTO) {
        Skill skill = new Skill();
        skill.setName(skillDTO.getName());

        Skill savedSkill = skillRepo.save(skill);
        return mapToDTO(savedSkill);
    }
    @Transactional
    public SkillDto updateSkill(Long id, SkillDto skillDTO) {
        // Търсим умението по ID. Ако го няма, хвърляме грешка.
        Skill skill = skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Умението не е намерено!"));

        // Обновяваме данните
        skill.setName(skillDTO.getName());
        skill.setDescription(skillDTO.getName());

        Skill updatedSkill = skillRepo.save(skill);
        return mapToDTO(updatedSkill);
    }
    private SkillDto mapToDTO(Skill skill) {
        SkillDto dto = new SkillDto();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        return dto;
    }
    @Override
    public Skill getSkillByCategory(String category) {
        return this.skillRepo.getSkillByCategory(category);
    }

    @Override
    public void seedCategories() {
        if (skillRepo.getSkillByCategory("it") == null) {
            Skill skill = new Skill();
            skill.setCategory("it");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("medicine") == null) {
            Skill skill = new Skill();
            skill.setCategory("medicine");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("archaeology") == null) {
            Skill skill = new Skill();
            skill.setCategory("archaeology");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("engineering") == null) {
            Skill skill = new Skill();
            skill.setCategory("engineering");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("education") == null) {
            Skill skill = new Skill();
            skill.setCategory("education");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("law") == null) {
            Skill skill = new Skill();
            skill.setCategory("law");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("art") == null) {
            Skill skill = new Skill();
            skill.setCategory("art");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("science") == null) {
            Skill skill = new Skill();
            skill.setCategory("science");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("business") == null) {
            Skill skill = new Skill();
            skill.setCategory("business");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("finance") == null) {
            Skill skill = new Skill();
            skill.setCategory("finance");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("mathematics") == null) {
            Skill skill = new Skill();
            skill.setCategory("mathematics");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("psychology") == null) {
            Skill skill = new Skill();
            skill.setCategory("psychology");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("architecture") == null) {
            Skill skill = new Skill();
            skill.setCategory("architecture");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("agriculture") == null) {
            Skill skill = new Skill();
            skill.setCategory("agriculture");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("sports") == null) {
            Skill skill = new Skill();
            skill.setCategory("sports");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("media") == null) {
            Skill skill = new Skill();
            skill.setCategory("media");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("music") == null) {
            Skill skill = new Skill();
            skill.setCategory("music");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("philosophy") == null) {
            Skill skill = new Skill();
            skill.setCategory("philosophy");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("history") == null) {
            Skill skill = new Skill();
            skill.setCategory("history");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("linguistics") == null) {
            Skill skill = new Skill();
            skill.setCategory("linguistics");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("environment") == null) {
            Skill skill = new Skill();
            skill.setCategory("environment");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("astronomy") == null) {
            Skill skill = new Skill();
            skill.setCategory("astronomy");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("geology") == null) {
            Skill skill = new Skill();
            skill.setCategory("geology");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("biology") == null) {
            Skill skill = new Skill();
            skill.setCategory("biology");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("chemistry") == null) {
            Skill skill = new Skill();
            skill.setCategory("chemistry");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("physics") == null) {
            Skill skill = new Skill();
            skill.setCategory("physics");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("economics") == null) {
            Skill skill = new Skill();
            skill.setCategory("economics");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("sociology") == null) {
            Skill skill = new Skill();
            skill.setCategory("sociology");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("politics") == null) {
            Skill skill = new Skill();
            skill.setCategory("politics");
            skillRepo.save(skill);
        }
        if (skillRepo.getSkillByCategory("transportation") == null) {
            Skill skill = new Skill();
            skill.setCategory("transportation");
            skillRepo.save(skill);
        }
    }


    @Override
    public Skill getSkillByName(String name) {
       return this.skillRepo.getSkillByName(name);
    }

    @Override
    public void addSkill(AddSkillDTO addSkillDto) {
        Skill skill=new Skill();
        skill.setName(addSkillDto.getName());
        skill.setTag(addSkillDto.getTag());
        skill.setCategory(String.valueOf(addSkillDto.getCategory()));
        skill.setDescription(addSkillDto.getDescription());
        skillRepo.save(skill);
    }
    @Override
    public void updateSkill(Skill updatedSkill) {
        // Find the existing skill by its ID to ensure we're updating, not creating.
        Skill existingSkill = skillRepo.findById(updatedSkill.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid skill ID: " + updatedSkill.getId()));


        existingSkill.setName(updatedSkill.getName());
        existingSkill.setCategory(updatedSkill.getCategory());


        skillRepo.save(existingSkill);
    }

    @Override
    public void deleteSkill(Long id) {
    skillRepo.deleteById(id);
    }

    @Override
    public Skill getSkillById(Long id) {
        return this.skillRepo.findById(id).orElse(null);
    }
    public SkillView getSkill(Long id){
    return mapper.map(getSkillById(id),SkillView.class);
    }


}
