package ute.bookstore.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    // Tạo khóa 512 bit sử dụng phương thức Keys.secretKeyFor(SignatureAlgorithm.HS512)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Khóa 512-bit an toàn cho HS512

    // Tạo token với username và roles
    public String generateToken(String username, Collection<String> roles) {
        System.out.println("Roles in token: " + roles); // Kiểm tra xem roles có đúng không
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512) // Use SignatureAlgorithm here
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            getClaimsFromToken(token);  // Nếu token không hợp lệ, sẽ ném ra exception
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Sử dụng khóa 512-bit
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Lấy danh sách roles từ token
    @SuppressWarnings("unchecked")  // Suppressing the unchecked cast warning
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        // Explicitly cast to List<String> to avoid type safety warning
        return (List<String>) claims.get("roles");  // Trích xuất danh sách quyền từ token
    }

    // Kiểm tra quyền
    public boolean hasRole(String token, String role) {
        List<String> roles = getRolesFromToken(token);
        return roles != null && roles.contains(role);
    }
    
}
