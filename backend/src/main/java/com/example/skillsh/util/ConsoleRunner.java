package com.example.skillsh.util;


import com.example.skillsh.repository.ServiceOfferRepository;
import com.example.skillsh.services.role.RoleServiceImpl;
import com.example.skillsh.services.skill.SkillServiceImpl;
import com.example.skillsh.services.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private SkillServiceImpl skillService;
    private RoleServiceImpl roleService;
    private UserServiceImpl userService;
    private ServiceOfferRepository offerRepository;
     @Autowired
    public ConsoleRunner(SkillServiceImpl skillService, RoleServiceImpl roleService, UserServiceImpl userService, ServiceOfferRepository offerRepository) {
        this.skillService = skillService;
         this.roleService = roleService;
         this.userService = userService;
         this.offerRepository = offerRepository;
     }
    @Override
    public void run(String... args) throws Exception {
         if(skillService.skills().isEmpty()){
             skillService.seedCategories();
         }if(roleService.roles().isEmpty()){
            roleService.seedRoles();
        }
    }
}
