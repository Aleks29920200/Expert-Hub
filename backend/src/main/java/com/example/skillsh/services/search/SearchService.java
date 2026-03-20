package com.example.skillsh.services.search;

import com.example.skillsh.domain.dto.search.SearchDto;
import com.example.skillsh.domain.entity.Skill;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {
   Skill findSearchedInformationByCategory(SearchDto searchDto);
}
