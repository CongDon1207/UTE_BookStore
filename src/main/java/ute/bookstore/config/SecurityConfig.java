package ute.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll() // Cho phép công khai các URL này
	            .requestMatchers("/**").permitAll()  
	        )
	        .formLogin(form -> form
	            .loginPage("/login") // URL của trang đăng nhập
	            .permitAll() // Cho phép truy cập trang login
	        )
	        .csrf(csrf -> csrf.disable()); // Tắt CSRF (nếu không cần thiết)
	    return http.build();
	}

}
