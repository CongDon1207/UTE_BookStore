package ute.bookstore.service;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ute.bookstore.entity.User;
import ute.bookstore.enums.UserRole;
import ute.bookstore.repository.UserRepository;

@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Chuyển đổi vai trò đơn lẻ thành GrantedAuthority
        GrantedAuthority authority = (GrantedAuthority) () -> "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(authority) // Đưa authority vào một danh sách
        );
    }


}
