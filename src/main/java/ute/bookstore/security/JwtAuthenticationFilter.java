package ute.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ute.bookstore.service.JwtService;
import ute.bookstore.service.UserDetailsServiceImpl;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization");

        // Kiểm tra xem token có hợp lệ và bắt đầu bằng "Bearer " không
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ phần "Bearer "

            // Kiểm tra tính hợp lệ của token
            if (jwtService.isValidToken(token)) {
                // Lấy username từ token
                String username = jwtService.getUsernameFromToken(token);

                // Load thông tin người dùng từ UserDetailsServiceImpl
                var userDetails = userDetailsService.loadUserByUsername(username);

                // Tạo đối tượng authentication token
                JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Set chi tiết yêu cầu vào đối tượng authentication
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Lưu thông tin xác thực vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Tiến hành tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }
}
