package com.example.skillsh.web;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.services.skill.SkillService;
import com.example.skillsh.services.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:4200")
public class SearchController {

    private final UserServiceImpl userService;
    private final SkillService skillService;

    @Autowired
    public SearchController(UserServiceImpl userService, SkillService skillService) {
        this.userService = userService;
        this.skillService = skillService;
    }

    // 1. Търсене по ключова дума (Име или Умение)
    // Преработено на GET заявка, която обработва празни заявки!
    @GetMapping
    public ResponseEntity<List<UserView>> searchUsers(@RequestParam(required = false, defaultValue = "") String query) {
        String trimmedQuery = query.trim();

        // Ако фронтендът прати празен стринг (както прави HomeComponent при зареждане)
        if (trimmedQuery.isEmpty()) {
            // Трябва да имаш метод в userService, който връща ВСИЧКИ потребители като UserView
            return ResponseEntity.ok(userService.users());
        }

        // Ако има ключова дума, търсим по нея
        List<UserView> users = userService.searchUsers(trimmedQuery);
        return ResponseEntity.ok(users);
    }

    // 2. Търсене по точна категория
    @GetMapping("/category/{category}")
    public ResponseEntity<List<UserView>> searchByCategory(@PathVariable String category) { // По-добре връщай UserView, а не User!
        Skill skill = skillService.getSkillByCategory(category);
        if (skill == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Тук също е силно препоръчително да връщаш View/DTO, за да избегнеш infinite recursion
        List<UserView> users = userService.findAllBySkills(Collections.singletonList(skill.getId()));
        return ResponseEntity.ok(users);
    }
}