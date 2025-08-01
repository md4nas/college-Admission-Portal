package com.m4nas.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user/")
public class UserController {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
    }

    @ModelAttribute
    private void userDetails(Model m, Principal p, HttpServletRequest request) {
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        m.addAttribute("currentPath", request.getRequestURI());
    }

    @GetMapping("/")
    public String redirectToHome(Principal p, Model model){
        if(p != null){
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            if(user != null) {
                model.addAttribute("user", user);
                return "user/home";
            } else {
                return "redirect:/signin?error";
            }
        }
        return "redirect:/signin";
    }



    @GetMapping("/settings/changePass")
    public String loadChangePassword() {
        return "user/settings/change_password";
    }

    @PostMapping("/settings/updatePassword")
    public String changePassword(Principal p,
                                 @RequestParam("oldPass") String oldPass,
                                 @RequestParam("newPass") String newPass,
                                 HttpSession session){

        String email=p.getName();
        UserDtls loginUser=userRepo.findByEmail(email);

       boolean f=passwordEncoder.matches(oldPass,loginUser.getPassword());

       if(f){
           loginUser.setPassword((passwordEncoder.encode(newPass)));
           UserDtls updatePasswordUser=userRepo.save(loginUser);
           if(updatePasswordUser != null){
               session.setAttribute("msg","Password changed successfully!");
               session.setAttribute("msgType", "success");
               // Clear message after 5 seconds to prevent auto-redirect on next visit
               new Thread(() -> {
                   try {
                       Thread.sleep(5000);
                       session.removeAttribute("msg");
                       session.removeAttribute("msgType");
                   } catch (InterruptedException e) {}
               }).start();
           }else{
               session.setAttribute("msg","Something went wrong. Please try again.");
               session.setAttribute("msgType", "danger");
           }
       }else{
           session.setAttribute("msg","Incorrect old password. Please try again.");
           session.setAttribute("msgType", "danger");
       }
        return "redirect:/user/settings/changePass";
    }
}

