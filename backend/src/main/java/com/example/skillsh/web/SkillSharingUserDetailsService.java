package com.example.skillsh.web;

import com.example.skillsh.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Service
public class SkillSharingUserDetailsService implements UserDetailsService {
    private UserRepo userRepo;
    public SkillSharingUserDetailsService(UserRepo userRepository) {
        this.userRepo=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user= userRepo.findUserByUsername(username);
        return user.map(u -> new User(
                        u.getUsername(),
                        u.getPassword(),
                        u.getRole().stream()
                                .map(r -> new SimpleGrantedAuthority(
                                        "ROLE_"+r.getName()))
                                .collect(Collectors.toSet())
                )).orElseThrow(() -> new UsernameNotFoundException(username + " was not found!"));
    }
}
