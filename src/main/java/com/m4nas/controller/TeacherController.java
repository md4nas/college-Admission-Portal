package com.m4nas.controller;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import com.m4nas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;

/**
 * Controller for teacher dashboard and teacher-specific operations.
 * Provides access to user management features for teachers.
 */
@Controller
@RequestMapping("/teacher/")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @ModelAttribute
    private void userDetails(Model m, Principal p, HttpServletRequest request) {
        if(p!=null) {
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
        m.addAttribute("currentPath", request.getRequestURI());
    }

    @GetMapping("/")
    public String home(Model model){
        List<UserDtls> users = userService.getUsersByRole("ROLE_USER");
        model.addAttribute("users", users);
        return "teacher/home";
    }
}
