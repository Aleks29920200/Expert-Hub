package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
    List<Role> getRoleByName(String name);

}
