package com.example.skillsh.configuration;

import com.example.skillsh.domain.dto.appointment.AddAppointment;
import com.example.skillsh.domain.dto.user.RegisterAsExpertDto;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.web.SkillSharingUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class Config {

    @Bean
    public RegisterAsExpertDto registerDto(){
        return new RegisterAsExpertDto();
    }
    @Bean
    public AddAppointment addAppointment(){
        return new AddAppointment();
    }

    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserView userView(){
        return new UserView();
    }
}
