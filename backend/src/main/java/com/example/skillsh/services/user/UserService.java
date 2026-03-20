package com.example.skillsh.services.user;

import com.example.skillsh.domain.dto.user.*;

import com.example.skillsh.domain.entity.User;
import com.example.skillsh.domain.entity.enums.Status;
import com.example.skillsh.domain.view.UserView;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService{

    void registerUserAsExpert(RegisterAsExpertDto registerAsExpertDto);

    void registerUserAsClient(RegisterAsClientDto registerAsExpertDto);

    Optional<User> findById(Long aLong);
    List<User> searchUsersByInfo(String keyword);

    User setUserStatus(User user);
     void updateStatus(String username, Status status);
    List<User> getAll();
    @EntityGraph(attributePaths = {"role"})
    UserProfileDTO getUserByUsername(String username);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    List<UserView> users();

    List<User>searchByFirstName(String firstName);

    List<UserView> findAllBySkills(List<Long> skillids);

    void registerO2AuthUser(OAuth2User oauth2User);

    void addUser(@Valid AddUserDTO addUserDto);

    void updateUser(AddUserDTO user);

    void deleteUser(Long id);

    List<User> getUsersWithReviews();

    UserDTO saveUser(UserDTO userDTO);

}

