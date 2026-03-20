package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.*;
import com.example.skillsh.domain.entity.enums.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = {"role", "skills"})
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);

    List<User> findAllByActivity(Status online);
    List<User> findAllBySkillsIn(Collection<List<Skill>> skills);
    List<User> findAllBySkills_IdIn(List<Long> skillIds);
    List<User>searchByFirstName(String firstName);
    @Query("SELECT u FROM User u JOIN u.role r JOIN u.skills s " +
            "WHERE r.name = 'ROLE_EXPERT' AND LOWER(s.category) = LOWER(:category)")
    List<User> findExpertsByCategory(@Param("category") String category);

    // Finds Experts by keyword in name or skill description
    @Query("SELECT u FROM User u JOIN u.role r LEFT JOIN u.skills s " +
            "WHERE r.name = 'ROLE_EXPERT' AND (" +
            "LOWER(u.firstName) LIKE LOWER(concat('%', :query, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(concat('%', :query, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(concat('%', :query, '%')))")
    List<User> searchExpertsByKeyword(@Param("query") String query);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String firstName, String lastName, String username);
    @Query("SELECT u FROM User u JOIN u.blockedUsers bu WHERE bu = :blockedUser")
    List<User> findUsersWhoBlocked(@Param("blockedUser") User blockedUser);



    Optional<User> findById(Long aLong);

}

