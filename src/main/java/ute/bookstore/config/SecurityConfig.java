package ute.bookstore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ute.bookstore.security.JwtAuthenticationFilter;
import ute.bookstore.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    // Cấu hình PasswordEncoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình AuthenticationManager từ AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Cấu hình AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // Set UserService cho provider
        authProvider.setPasswordEncoder(passwordEncoder()); // Set PasswordEncoder
        return authProvider;
    }

    // Cấu hình các yêu cầu HTTP với các quyền truy cập và JWT filter
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF cho đơn giản
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll() // Các tệp tĩnh công khai
                .requestMatchers("/api/auth/**").permitAll() // Các API xác thực công khai
                .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ admin mới truy cập
                .requestMatchers("/vendor/**").hasRole("VENDOR") // Chỉ vendor mới truy cập
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // User và Admin có quyền truy cập
                .anyRequest().authenticated() // Các yêu cầu còn lại yêu cầu đăng nhập
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Thêm JWT filter vào chuỗi filter
            .formLogin(form -> form
                .loginPage("/login") // Trang đăng nhập tùy chỉnh
                .permitAll()
            );

        return http.build();
    }
}
