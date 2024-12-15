package ute.bookstore.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {


    @GetMapping("/home")
    public String Home() {
        return "index" ;
    }
    @GetMapping("/login")
    public String login() {
        return "login" ;
    }
    
}
