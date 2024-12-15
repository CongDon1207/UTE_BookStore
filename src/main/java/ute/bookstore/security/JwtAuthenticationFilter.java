package ute.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ute.bookstore.service.JwtService;
import ute.bookstore.service.UserDetailsServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Lấy token từ header Authorization
        String token = extractToken(request);
        
        // Log header để kiểm tra token
        System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        
        // Kiểm tra nếu token hợp lệ
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Bỏ prefix "Bearer "
            System.out.println("Token received: " + token);

            // Xác thực token
            if (jwtService.isValidToken(token)) {
            	System.out.println("CO TOKEN");
                String username = jwtService.getUsernameFromToken(token);
                List<String> roles = jwtService.getRolesFromToken(token);

                // Chuyển roles thành quyền cho người dùng
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Lấy thông tin người dùng từ username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Tạo đối tượng xác thực
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                // Cung cấp chi tiết authentication từ request
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đặt đối tượng xác thực vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                System.out.println("Authentication successful for user: " + username);
            } else {
                System.out.println("Invalid token");
            }
        } else {
            System.out.println("No token found in the request");
        }

        // Tiếp tục xử lý request
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Lấy token từ header "Authorization"
        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + header); // Kiểm tra giá trị header

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // Extract token part after "Bearer "
        }
        return null;
    }
}

