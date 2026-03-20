package com.example.skillsh.configuration;

import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.web.SkillSharingUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This tells Spring Boot: "If a URL requests /uploads/**, look in the local uploads/ folder"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().
                cors(cors -> cors.configurationSource(corsConfigurationSource())).
                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).
                authorizeRequests().
                requestMatchers("/ws/**").permitAll().
                requestMatchers("/api/auth/index","oauth2/authorization/facebook").permitAll().
                requestMatchers("/api/search/**").permitAll().
                requestMatchers("/api/auth/register/**", "/api/auth/login","/api/auth/me").permitAll().
                requestMatchers("api/auth/home/**").fullyAuthenticated().
                requestMatchers("/api/users/admin/**").hasRole("ADMIN").
                requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN").
                requestMatchers("/api/skills/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN").
                requestMatchers("/api/users/block/**").authenticated().
                requestMatchers("logout").authenticated().
                requestMatchers("/").denyAll().
                and()
                .oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?oauth2error=true")
                .and()
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Вместо 302 Redirect към /login, връщаме чист 401 Unauthorized
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                        })
                );
      return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow your Angular Frontend
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow headers (Authorization, Content-Type, etc.)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));

        // Allow credentials (cookies/auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
