package com.m4nas.controller;

import com.m4nas.model.UserDtls;
import com.m4nas.model.UserApplication;
import com.m4nas.repository.UserRepository;
import com.m4nas.service.UserService;
import com.m4nas.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserApplicationService userApplicationService;

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
        try {
            List<UserDtls> allUsers = userService.getAllUsers();
            List<UserDtls> teachers = userService.getTeachers();

            // Debug logging
            System.out.println("=== ADMIN DEBUG ===");
            System.out.println("All users count: " + (allUsers != null ? allUsers.size() : "null"));
            System.out.println("Teachers count: " + (teachers != null ? teachers.size() : "null"));
            if (allUsers != null && !allUsers.isEmpty()) {
                System.out.println("First user: " + allUsers.get(0).getEmail() + " - " + allUsers.get(0).getRole());
            }

            // Application statistics with null safety
            List<UserApplication> allApplications = userApplicationService.getAllApplicationsForAdmin();
            List<Object[]> statusCounts = userApplicationService.getApplicationStatusCounts();
            List<Object[]> branchStats = userApplicationService.getBranchWiseStatistics();

            model.addAttribute("users", allUsers != null ? allUsers : new java.util.ArrayList<>());
            model.addAttribute("teachers", teachers != null ? teachers : new java.util.ArrayList<>());
            model.addAttribute("applications", allApplications != null ? allApplications : new java.util.ArrayList<>());
            model.addAttribute("statusCounts", statusCounts != null ? statusCounts : new java.util.ArrayList<>());
            model.addAttribute("branchStats", branchStats != null ? branchStats : new java.util.ArrayList<>());

            return "admin/home";
        } catch (Exception e) {
            System.err.println("Error in admin home: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading admin dashboard: " + e.getMessage());
            return "admin/home";
        }
    }

    @GetMapping("/applications")
    public String viewAllApplications(Model model) {
        try {
            List<UserApplication> applications = userApplicationService.getAllApplicationsForAdmin();
            model.addAttribute("applications", applications != null ? applications : new java.util.ArrayList<>());
            return "admin/applications";
        } catch (Exception e) {
            System.err.println("Error in admin applications: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("applications", new java.util.ArrayList<>());
            model.addAttribute("error", "Error loading applications: " + e.getMessage());
            return "admin/applications";
        }
    }

    @GetMapping("/applications/status")
    public String viewApplicationsByStatus(@RequestParam("status") String status, Model model) {
        List<UserApplication> applications = userApplicationService.getApplicationsByStatus(status);
        model.addAttribute("applications", applications);
        model.addAttribute("filterStatus", status);
        return "admin/applications";
    }

    @GetMapping("/statistics")
    public String viewStatistics(Model model){
        try {
            List<Object[]> statusCounts = userApplicationService.getApplicationStatusCounts();
            List<Object[]> branchStats = userApplicationService.getBranchWiseStatistics();
            List<UserApplication> allApplications = userApplicationService.getAllApplicationsForAdmin();
            
            // Sort applications by marks in descending order for top performers
            if (allApplications != null) {
                allApplications.sort((a, b) -> Integer.compare(b.getObtain12Marks(), a.getObtain12Marks()));
            }

            model.addAttribute("statusCounts", statusCounts != null ? statusCounts : new java.util.ArrayList<>());
            model.addAttribute("branchStats", branchStats != null ? branchStats : new java.util.ArrayList<>());
            model.addAttribute("applications", allApplications != null ? allApplications : new java.util.ArrayList<>());

            return "admin/statistics";
        } catch (Exception e) {
            System.err.println("Error in admin statistics: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("statusCounts", new java.util.ArrayList<>());
            model.addAttribute("branchStats", new java.util.ArrayList<>());
            model.addAttribute("applications", new java.util.ArrayList<>());
            model.addAttribute("error", "Error loading statistics: " + e.getMessage());
            return "admin/statistics";
        }
    }
}
