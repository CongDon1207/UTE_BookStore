package ute.bookstore.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_ADMIN")) {
            response.sendRedirect("/admin/home");
        } else if (role.equals("ROLE_VENDOR")) {
            response.sendRedirect("/vendor/home");
        } else if (role.equals("ROLE_USER")) {
            response.sendRedirect("/user/home");
        } else {
            response.sendRedirect("/home"); // Mặc định nếu không xác định được vai trò
        }
    }
}
