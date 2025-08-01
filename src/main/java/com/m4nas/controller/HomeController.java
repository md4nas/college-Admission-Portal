package com.m4nas.controller;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import com.m4nas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private final UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public HomeController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @ModelAttribute
    private void userDetails(Model m, Principal p) {
        if(p!=null) {
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @GetMapping("/signin")
    public String login() {
        return "signin";
    }

    @PostMapping("/createUser")
    public String createuser(@ModelAttribute UserDtls user,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {


        String url = request.getRequestURL().toString();
        url = url.replace(request.getServletPath(), "");
        
        System.out.println("Registration URL: " + url);
        System.out.println("User email: " + user.getEmail());

        boolean f = userService.checkEmail(user.getEmail());

        if(f) {
            redirectAttributes.addFlashAttribute("msg","Email id already exist");
        } else {
            try {
                UserDtls userDtls = userService.createUser(user, url);
                if(userDtls != null) {
                    System.out.println("User created successfully: " + userDtls.getEmail());
                    redirectAttributes.addFlashAttribute("msg","Registration Successful! Check your email for verification.");
                } else {
                    redirectAttributes.addFlashAttribute("msg","Something Error in Server");
                }
            } catch (Exception e) {
                System.out.println("Error during user creation: " + e.getMessage());
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("msg","Error sending verification email: " + e.getMessage());
            }
        }
        return "redirect:/register";
    }



    @GetMapping("/loadResetPassword/{id}")
    public String loadResetPassword(@PathVariable String id, Model m){
        m.addAttribute("id",id);
        return "reset_password";
    }

    @GetMapping("/loadForgetPassword")
    public String loadForgetPassword(){
        return "redirect:/forgot-password";
    }



}