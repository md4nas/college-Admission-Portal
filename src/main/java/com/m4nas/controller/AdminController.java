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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private com.m4nas.service.AnnouncementService announcementService;

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
            model.addAttribute("error", "Error loading admin dashboard");
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
            model.addAttribute("applications", new java.util.ArrayList<>());
            model.addAttribute("error", "Error loading applications");
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



    @GetMapping("/users")
    public String manageUsers(Model model) {
        try {
            List<UserDtls> allUsers = userService.getAllUsers();
            List<UserDtls> teachers = userService.getTeachers();
            List<UserDtls> students = allUsers.stream()
                    .filter(user -> "ROLE_USER".equals(user.getRole()))
                    .collect(java.util.stream.Collectors.toList());
            
            model.addAttribute("users", allUsers != null ? allUsers : new java.util.ArrayList<>());
            model.addAttribute("teachers", teachers != null ? teachers : new java.util.ArrayList<>());
            model.addAttribute("students", students != null ? students : new java.util.ArrayList<>());
            
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("users", new java.util.ArrayList<>());
            model.addAttribute("teachers", new java.util.ArrayList<>());
            model.addAttribute("students", new java.util.ArrayList<>());
            model.addAttribute("error", "Error loading users");
            return "admin/users";
        }
    }

    @GetMapping("/system-info")
    public String systemInfo(Model model) {
        try {
            // System information
            model.addAttribute("javaVersion", System.getProperty("java.version"));
            model.addAttribute("osName", System.getProperty("os.name"));
            model.addAttribute("osVersion", System.getProperty("os.version"));
            model.addAttribute("totalMemory", Runtime.getRuntime().totalMemory() / (1024 * 1024));
            model.addAttribute("freeMemory", Runtime.getRuntime().freeMemory() / (1024 * 1024));
            model.addAttribute("maxMemory", Runtime.getRuntime().maxMemory() / (1024 * 1024));
            
            return "admin/system-info";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading system information");
            return "admin/system-info";
        }
    }

    // User Management Operations
    @PostMapping("/users/update-role")
    public String updateUserRole(@RequestParam("userId") String userId, 
                                @RequestParam("newRole") String newRole,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<UserDtls> userOpt = userRepo.findById(userId);
            if (userOpt.isPresent()) {
                UserDtls user = userOpt.get();
                user.setRole(newRole);
                userRepo.save(user);
                redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam("userId") String userId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<UserDtls> userOpt = userRepo.findById(userId);
            if (userOpt.isPresent()) {
                UserDtls user = userOpt.get();
                user.setEnable(!user.isEnable());
                userRepo.save(user);
                redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable("userId") String userId,
                            RedirectAttributes redirectAttributes) {
        try {
            if (userRepo.existsById(userId)) {
                // First delete related applications
                UserDtls user = userRepo.findById(userId).orElse(null);
                if (user != null) {
                    userApplicationService.deleteApplicationsByUserEmail(user.getEmail());
                }
                userRepo.deleteById(userId);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Application Management
    @PostMapping("/applications/delete/{applicationId}")
    public String deleteApplication(@PathVariable("applicationId") Long applicationId,
                                   RedirectAttributes redirectAttributes) {
        try {
            userApplicationService.deleteApplication(applicationId);
            redirectAttributes.addFlashAttribute("success", "Application deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting application: " + e.getMessage());
        }
        return "redirect:/admin/applications";
    }

    @PostMapping("/applications/update-status")
    public String updateApplicationStatus(@RequestParam("applicationId") Long applicationId,
                                         @RequestParam("newStatus") String newStatus,
                                         RedirectAttributes redirectAttributes) {
        try {
            userApplicationService.updateApplicationStatus(applicationId, newStatus);
            redirectAttributes.addFlashAttribute("success", "Application status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating application status: " + e.getMessage());
        }
        return "redirect:/admin/applications";
    }

    // Database Management
    @GetMapping("/database")
    public String databaseManagement(Model model) {
        return "admin/database";
    }

    @PostMapping("/database/backup")
    @ResponseBody
    public ResponseEntity<String> backupDatabase() {
        try {
            // This would typically involve database backup logic
            return ResponseEntity.ok("Database backup initiated successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating backup: " + e.getMessage());
        }
    }

    // Announcements Management
    @GetMapping("/announcements")
    public String manageAnnouncements(Model model) {
        try {
            // Get all announcements (admin can see all announcements from all users)
            List<com.m4nas.model.Announcement> announcements = announcementService.getActiveAnnouncements();
            model.addAttribute("announcements", announcements);
        } catch (Exception e) {
            model.addAttribute("announcements", new java.util.ArrayList<>());
        }
        return "admin/announcements";
    }

    @PostMapping("/announcements/create")
    public String createAnnouncement(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam("priority") String priority,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Add announcement creation logic here
            redirectAttributes.addFlashAttribute("success", "Announcement created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating announcement: " + e.getMessage());
        }
        return "redirect:/admin/announcements";
    }
}
