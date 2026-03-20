package com.example.skillsh.configuration;




import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {



    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // --- ДОБАВЕНИ ЛОГОВЕ ЗА ПРОСЛЕДЯВАНЕ ---
        System.out.println("\n--- НОВА ЗАЯВКА КЪМ: " + request.getRequestURI() + " ---");
        System.out.println("Хедър Authorization: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("-> Внимание: Няма токен или не започва с Bearer. Продължаваме като анонимен.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = token.replace("test-token-for-", "").trim();
        System.out.println("-> Извлечено име от токена: " + username);

        if (!username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("-> Потребителят е намерен в базата! Неговите роли са: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("-> УСПЕХ: Баджът е поставен! Пускаме го навътре.");

            } catch (Exception e) {
                System.out.println("-> ГРЕШКА: Потребителят не е намерен в базата: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
