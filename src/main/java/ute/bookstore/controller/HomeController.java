package ute.bookstore.controller;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ute.bookstore.dto.AuthRequest;
import ute.bookstore.dto.LoginResponse;
import ute.bookstore.service.JwtService;
import ute.bookstore.util.JwtUtil;

@Controller
public class HomeController {

    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtService jwtService;
    @GetMapping("/home")
    public String Home() {
        return "index" ;
    }
    @GetMapping("/login")
    public String login() {
        return "login" ;
    }
    
}
