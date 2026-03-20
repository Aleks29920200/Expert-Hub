package com.example.skillsh.services.search;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.entity.Skill;
import com.example.skillsh.services.skill.SkillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {
    private SkillServiceImpl skillService;
@Autowired
    public SearchServiceImpl(SkillServiceImpl skillService) {
        this.skillService = skillService;
    }

    // В SearchService интерфейса също трябва да го промениш от void на Skill
    @Override
    public Skill findSearchedInformationByCategory(SearchDto searchDto) {
        return skillService.getSkillByName(searchDto.getInfo());
    }
}
