package ute.bookstore.config;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ute.bookstore.security.CustomAuthenticationSuccessHandler;
import ute.bookstore.security.JwtAuthenticationFilter;
import ute.bookstore.service.JwtService;
import ute.bookstore.service.UserDetailsServiceImpl;
import ute.bookstore.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu không cần thiết
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                .requestMatchers("/auth/**").permitAll()  // Cho phép truy cập trang đăng nhập và các URL liên quan
                .requestMatchers("/home").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/vendor/**").hasRole("VENDOR")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")  // Cấu hình trang đăng nhập là /auth/login
                .permitAll()  // Cho phép truy cập vào trang login mà không cần đăng nhập
                .successHandler(customAuthenticationSuccessHandler)  // Xử lý sau khi đăng nhập thành công
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  // URL logout
                .logoutSuccessUrl("/auth/login")  // Sau khi logout, chuyển hướng về trang đăng nhập
            )
            .exceptionHandling(e -> e
                .accessDeniedPage("/access-denied")  // Trang lỗi khi quyền truy cập bị từ chối
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép các origin từ frontend của bạn
        configuration.setAllowedOrigins(List.of("http://localhost:9090")); // Thêm URL frontend của bạn ở đây
        // Các phương thức HTTP được phép
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Các headers được phép
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Cho phép gửi cookies hoặc các thông tin bảo mật khác
        configuration.setAllowCredentials(true);  
        // Thời gian hết hạn của CORS
        configuration.setMaxAge(3600L);

        // Cấu hình cho tất cả các endpoint
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}






