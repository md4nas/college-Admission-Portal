package com.m4nas.controller;

import com.m4nas.model.UserApplication;
import com.m4nas.service.UserApplicationService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;


@Controller
@RequestMapping("/user/")
public class UserController {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private UserApplicationService userApplicationService;

    @Autowired
    public UserController(UserRepository userRepo, PasswordEncoder passwordEncoder, UserApplicationService userApplicationService) {
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
        this.userApplicationService = userApplicationService;
    }

    @ModelAttribute
    private void userDetails(Model m, Principal p, HttpServletRequest request) {
        if(p != null) {
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
        m.addAttribute("currentPath", request.getRequestURI());
    }

    @GetMapping("/")
    public String redirectToHome(Principal p, Model model){
        if(p != null){
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            if(user != null) {
                //Adding Application status to user home page
                UserApplication application = userApplicationService.getUserApplicationByEmail(email);
                
                // Debug logging for home page
                System.out.println("=== USER HOME DEBUG ===");
                System.out.println("User: " + email);
                System.out.println("Application on home: " + (application != null ? application.getStatus() : "null"));

                model.addAttribute("user", user);
                model.addAttribute("application", application);
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

    @GetMapping("/application")
    public String newApplication(Principal p,Model model){
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);

        //check if user already had an application
        UserApplication existingApp = userApplicationService.getUserApplicationByEmail(email);
        if(existingApp != null){
            // Redirect to home page where status is shown
            return "redirect:/user/";
        }
        model.addAttribute("application", new UserApplication());
        model.addAttribute("user", user);
        return "user/new_application";
    }

    @PostMapping("/application/submit")
    public String submitApplication(@ModelAttribute UserApplication application,
                                    Principal p,HttpSession session){
        String email = p.getName();
        application.setUserEmail(email);

        try{
            // Debug logging
            System.out.println("Submitting application for user: " + email);
            System.out.println("Application data: " + application.getCourse() + ", " + application.getBranch1());
            
            UserApplication savedApp = userApplicationService.saveAcademicInfo(application);
            if(savedApp != null){
                session.setAttribute("msg","Application submitted successfully!");
                session.setAttribute("msgType","success");
            } else {
                session.setAttribute("msg","Failed to submit application. Please try again");
                session.setAttribute("msgType","danger");
            }
        } catch (Exception e){
            // Log the actual error
            System.err.println("Error submitting application: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("msg","Error occurred while submitting application: " + e.getMessage());
            session.setAttribute("msgType","danger");
        }
        return "redirect:/user/";
    }

    @GetMapping("/application/status")
    public String applicationStatus(Principal p, Model model){
        try {
            if(p == null) {
                return "redirect:/signin";
            }
            
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            UserApplication application = userApplicationService.getUserApplicationByEmail(email);

            model.addAttribute("user", user);
            model.addAttribute("application", application);
            
            // Debug logging
            System.out.println("=== APPLICATION STATUS DEBUG ===");
            System.out.println("User email: " + email);
            System.out.println("User found: " + (user != null ? user.getEmail() : "null"));
            System.out.println("Application found: " + (application != null ? "YES" : "NO"));
            if (application != null) {
                System.out.println("Application ID: " + application.getId());
                System.out.println("Application Status: " + application.getStatus());
                System.out.println("Application Course: " + application.getCourse());
            }
            
            return "user/application_status";
        } catch (Exception e) {
            System.err.println("Error in application status: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading application status: " + e.getMessage());
            return "user/application_status";
        }
    }

    @PostMapping("/application/accept-seat")
    public String acceptSeat(Principal p, HttpSession session){
        String email = p.getName();
        UserApplication application = userApplicationService.getUserApplicationByEmail(email);

        if(application != null && "ALLOCATED".equals(application.getStatus())){
            UserApplication updatedApplication = userApplicationService.acceptSeat(application.getId());
            if(updatedApplication != null){
                session.setAttribute("msg", "Seat accepted successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to accept seat. Please try again.");
                session.setAttribute("msgType", "danger");
            }
        } else {
            session.setAttribute("msg","No Seat allocation found!");
            session.setAttribute("msgType","warning");
        }
        return "redirect:/user/";
    }

    @PostMapping("/application/decline-seat")
    public String declineSeat(Principal p, HttpSession session){
        String email = p.getName();
        UserApplication application = userApplicationService.getUserApplicationByEmail(email);

        if(application != null && "ALLOCATED".equals(application.getStatus())){
            UserApplication updatedApplication = userApplicationService.declineSeat(application.getId());
            if(updatedApplication != null){
                session.setAttribute("msg", "Seat declined successfully!");
                session.setAttribute("msgType", "info");
            } else {
                session.setAttribute("msg", "Failed to decline seat. Please try again.");
                session.setAttribute("msgType", "danger");
            }
        } else {
            session.setAttribute("msg","No Seat allocation found or invalid status");
            session.setAttribute("msgType","warning");
        }
        return "redirect:/user/";
    }
}
