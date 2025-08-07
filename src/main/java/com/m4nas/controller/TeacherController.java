package com.m4nas.controller;

import com.m4nas.model.UserDtls;
import com.m4nas.model.UserApplication;
import com.m4nas.model.Announcement;
import com.m4nas.repository.UserRepository;
import com.m4nas.service.UserService;
import com.m4nas.service.UserApplicationService;
import com.m4nas.service.AnnouncementService;
import com.m4nas.service.PaymentService;
import com.m4nas.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teacher/")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private PaymentService paymentService;

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
        // Dashboard Statistics
        List<UserApplication> pendingApplications = userApplicationService.getApplicationsPendingApproval();
        List<UserApplication> approvedApplications = userApplicationService.getApprovedApplicationsForSeatAllocation();
        List<UserApplication> allocatedApplications = userApplicationService.getAllocatedApplicationsPendingResponse();
        List<Object[]> statusCounts = userApplicationService.getApplicationStatusCounts();
        List<Object[]> branchStats = userApplicationService.getBranchWiseStatistics();
        
        // Recent Users
        List<UserDtls> recentUsers = userService.getUsersByRole("ROLE_USER");
        
        // Recent Announcements (top 10 for scroll)
        List<Announcement> recentAnnouncements = announcementService.getActiveAnnouncements().stream().limit(10).collect(java.util.stream.Collectors.toList());
        
        System.out.println("=== TEACHER DASHBOARD DEBUG ===");
        System.out.println("Recent announcements count: " + recentAnnouncements.size());
        for (Announcement ann : recentAnnouncements) {
            System.out.println("- " + ann.getTitle() + " by " + ann.getCreatedBy());
        }
        
        // Add to model
        model.addAttribute("pendingCount", pendingApplications.size());
        model.addAttribute("approvedCount", approvedApplications.size());
        model.addAttribute("allocatedCount", allocatedApplications.size());
        model.addAttribute("recentApplications", pendingApplications.stream().limit(7).collect(java.util.stream.Collectors.toList()));
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("branchStats", branchStats);
        model.addAttribute("recentUsers", recentUsers.stream().limit(7).collect(java.util.stream.Collectors.toList()));
        model.addAttribute("recentAnnouncements", recentAnnouncements);
        
        return "teacher/home";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model) {
        List<UserApplication> applications = userApplicationService.getApplicationsPendingApproval();
        // Sort by class 12 percentage in descending order
        applications.sort((a, b) -> {
            Double percentageA = a.getPercentage12() != null ? a.getPercentage12() : 0.0;
            Double percentageB = b.getPercentage12() != null ? b.getPercentage12() : 0.0;
            return Double.compare(percentageB, percentageA);
        });
        model.addAttribute("applications", applications);
        return "teacher/applications";
    }

    @GetMapping("/applications/approve")
    public String viewApprovedApplications(Model model) {
        List<UserApplication> applications = userApplicationService.getApprovedApplicationsForSeatAllocation();
        model.addAttribute("applications", applications);
        return "teacher/seat_allocation";
    }

    @PostMapping("/applications/approve")
    public String approveApplication(@RequestParam("applicationId") String applicationId,
                                     HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.approveApplication(applicationId);
            if(updatedApp != null) {
                session.setAttribute("msg", "Application approved successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to approve application.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error occurred while approving application.");
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/applications";
    }

    @PostMapping("/applications/reject")
    public String rejectApplication(@RequestParam("applicationId") String applicationId,
                                    HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.rejectApplication(applicationId);
            if(updatedApp != null) {
                session.setAttribute("msg", "Application rejected.");
                session.setAttribute("msgType", "info");
            } else {
                session.setAttribute("msg", "Failed to reject application.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error occurred while rejecting application.");
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/applications";
    }

    @PostMapping("/applications/allocate-seat")
    public String allocateSeat(@RequestParam("applicationId") String applicationId,
                               @RequestParam("allocatedBranch") String allocatedBranch,
                               HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.allocateSeat(applicationId, allocatedBranch);
            if(updatedApp != null) {
                session.setAttribute("msg", "Seat allocated successfully to " + allocatedBranch + " branch!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to allocate seat.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error occurred while allocating seat.");
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/applications/approved";
    }

    @GetMapping("/application-details/{applicationId}")
    public String viewApplicationDetails(@PathVariable("applicationId") String applicationId, Model model) {
        UserApplication application = userApplicationService.getApplicationById(applicationId);
        
        if (application != null) {
            // Get user details for the application
            UserDtls user = userRepo.findByEmail(application.getUserEmail());
            model.addAttribute("user", user);
        }
        
        model.addAttribute("userApplication", application);
        return "teacher/application_details";
    }

    @PostMapping("/announcements/create")
    public String createAnnouncement(@RequestParam("title") String title,
                                     @RequestParam("content") String content,
                                     @RequestParam("targetAudience") String targetAudience,
                                     @RequestParam("announcementType") String announcementType,
                                     @RequestParam(value = "eventDate", required = false) String eventDate,
                                     @RequestParam(value = "eventTime", required = false) String eventTime,
                                     Principal principal,
                                     HttpSession session) {
        try {
            System.out.println("=== ANNOUNCEMENT CREATION DEBUG ===");
            System.out.println("Title: " + title);
            System.out.println("Content: " + content);
            System.out.println("Target Audience: " + targetAudience);
            System.out.println("Type: " + announcementType);
            System.out.println("Created By: " + principal.getName());
            
            Announcement announcement = new Announcement(title, content, principal.getName(), targetAudience);
            announcement.setAnnouncementType(announcementType);
            
            if (eventDate != null && !eventDate.isEmpty()) {
                announcement.setEventDate(java.time.LocalDate.parse(eventDate));
                System.out.println("Event Date: " + eventDate);
            }
            if (eventTime != null && !eventTime.isEmpty()) {
                announcement.setEventTime(eventTime);
                System.out.println("Event Time: " + eventTime);
            }
            
            Announcement savedAnnouncement = announcementService.saveAnnouncement(announcement);
            if (savedAnnouncement != null) {
                System.out.println("Announcement saved successfully with ID: " + savedAnnouncement.getId());
                session.setAttribute("msg", "Announcement created successfully!");
                session.setAttribute("msgType", "success");
            } else {
                System.err.println("Failed to save announcement - returned null");
                session.setAttribute("msg", "Failed to create announcement.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            System.err.println("Error creating announcement: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("msg", "Error creating announcement: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/";
    }

    @GetMapping("/announcements")
    public String viewAnnouncements(Model model, Principal principal) {
        List<Announcement> announcements = announcementService.getAnnouncementsByCreator(principal.getName());
        
        System.out.println("=== TEACHER ANNOUNCEMENTS PAGE DEBUG ===");
        System.out.println("Teacher: " + principal.getName());
        System.out.println("Announcements by teacher: " + announcements.size());
        for (Announcement ann : announcements) {
            System.out.println("- " + ann.getTitle() + " (" + ann.getCreatedAt() + ")");
        }
        
        model.addAttribute("announcements", announcements);
        return "teacher/announcements";
    }

    @GetMapping("/seat-management")
    public String seatManagement(Model model) {
        List<UserApplication> applications = userApplicationService.getAllApplications();
        
        System.out.println("=== SEAT MANAGEMENT DEBUG ===");
        System.out.println("Total applications: " + applications.size());
        for (UserApplication app : applications) {
            System.out.println("App ID: " + app.getId() + ", Email: " + app.getUserEmail() + ", Status: " + app.getStatus());
            System.out.println("  - Course: " + app.getCourse());
            System.out.println("  - Parents Name: " + app.getParentsName());
            System.out.println("  - Phone: " + app.getPhoneNo());
            System.out.println("  - DOB: " + app.getDob());
            System.out.println("  - Gender: " + app.getGender());
            System.out.println("  - Address: " + app.getAddress());
            System.out.println("  - Branch1: " + app.getBranch1());
            System.out.println("  - Branch2: " + app.getBranch2());
            System.out.println("  - Class12 Physics: " + app.getClass12Physics());
            System.out.println("  - Percentage12: " + app.getPercentage12());
            System.out.println("  ---");
        }
        
        model.addAttribute("applications", applications);
        return "teacher/seat_management";
    }

    @PostMapping("/seat-management/update-status")
    public String updateApplicationStatus(@RequestParam("applicationId") String applicationId,
                                          @RequestParam("status") String status,
                                          HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.updateApplicationStatus(applicationId, status);
            if(updatedApp != null) {
                session.setAttribute("msg", "Status updated successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to update status.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error updating status: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/seat-management";
    }

    @PostMapping("/seat-management/update-course")
    public String updateApplicationCourse(@RequestParam("applicationId") String applicationId,
                                          @RequestParam("course") String course,
                                          HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.updateApplicationCourse(applicationId, course);
            if(updatedApp != null) {
                session.setAttribute("msg", "Course updated successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to update course.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error updating course: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/seat-management";
    }

    @PostMapping("/seat-management/update-branch")
    public String updateApplicationBranch(@RequestParam("applicationId") String applicationId,
                                          @RequestParam("allocatedBranch") String allocatedBranch,
                                          HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.updateApplicationBranch(applicationId, allocatedBranch);
            if(updatedApp != null) {
                session.setAttribute("msg", "Branch updated successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to update branch.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error updating branch: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/seat-management";
    }

    @PostMapping("/seat-management/bulk-update")
    public String bulkUpdateApplications(HttpServletRequest request, HttpSession session) {
        try {
            int successCount = 0;
            int totalChanges = 0;
            
            // Get all parameters that start with "changes["
            java.util.Map<String, String[]> paramMap = request.getParameterMap();
            java.util.Map<String, java.util.Map<String, String>> changesByApp = new java.util.HashMap<>();
            
            // Parse the changes parameters
            for (String paramName : paramMap.keySet()) {
                if (paramName.startsWith("changes[")) {
                    String[] values = paramMap.get(paramName);
                    if (values.length > 0) {
                        // Extract index and field from parameter name
                        // Format: changes[0].appId, changes[0].field, changes[0].value
                        String indexPart = paramName.substring(8, paramName.indexOf(']'));
                        String fieldPart = paramName.substring(paramName.indexOf('.') + 1);
                        
                        changesByApp.computeIfAbsent(indexPart, k -> new java.util.HashMap<>()).put(fieldPart, values[0]);
                    }
                }
            }
            
            // Process each change
            for (java.util.Map<String, String> change : changesByApp.values()) {
                String appId = change.get("appId");
                String field = change.get("field");
                String value = change.get("value");
                
                if (appId != null && field != null && value != null) {
                    totalChanges++;
                    UserApplication updatedApp = null;
                    
                    switch (field) {
                        case "status":
                            updatedApp = userApplicationService.updateApplicationStatus(appId, value);
                            break;
                        case "course":
                            updatedApp = userApplicationService.updateApplicationCourse(appId, value);
                            break;
                        case "allocatedBranch":
                            updatedApp = userApplicationService.updateApplicationBranch(appId, value);
                            break;
                    }
                    
                    if (updatedApp != null) {
                        successCount++;
                    }
                }
            }
            
            if (totalChanges == 0) {
                session.setAttribute("msg", "No changes to save!");
                session.setAttribute("msgType", "info");
            } else if (successCount == totalChanges) {
                session.setAttribute("msg", "All " + successCount + " changes saved successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", successCount + " out of " + totalChanges + " changes saved successfully.");
                session.setAttribute("msgType", "warning");
            }
            
        } catch (Exception e) {
            session.setAttribute("msg", "Error saving changes: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        
        return "redirect:/teacher/seat-management";
    }
    
    @GetMapping("/payment-management")
    public String paymentManagement(Model model, HttpServletRequest request) {
        List<Payment> payments = paymentService.getAllPayments();
        
        // Debug: Print payment details
        System.out.println("=== PAYMENT DEBUG ===");
        for (Payment payment : payments) {
            System.out.println("Payment ID: " + payment.getId() + ", Status: " + payment.getStatus() + ", Student: " + payment.getStudentName());
        }
        
        // Calculate statistics
        long pendingCount = payments.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.PENDING).count();
        long verifiedCount = payments.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.VERIFIED).count();
        long rejectedCount = payments.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.REJECTED).count();
        
        model.addAttribute("payments", payments);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("verifiedCount", verifiedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("totalCount", payments.size());
        model.addAttribute("currentPath", request.getRequestURI());
        
        return "teacher/payment_management";
    }
    
    @PostMapping("/payment/verify/{id}")
    public String verifyPayment(@PathVariable Long id, Principal p, HttpSession session) {
        try {
            String teacherEmail = p.getName();
            paymentService.verifyPayment(id, teacherEmail);
            session.setAttribute("msg", "Payment verified successfully!");
            session.setAttribute("msgType", "success");
        } catch (Exception e) {
            session.setAttribute("msg", "Error verifying payment: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/payment-management";
    }
    
    @PostMapping("/payment/reject/{id}")
    public String rejectPayment(@PathVariable Long id, Principal p, HttpSession session) {
        try {
            String teacherEmail = p.getName();
            paymentService.rejectPayment(id, teacherEmail);
            session.setAttribute("msg", "Payment rejected successfully!");
            session.setAttribute("msgType", "warning");
        } catch (Exception e) {
            session.setAttribute("msg", "Error rejecting payment: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/payment-management";
    }
    
    @PostMapping("/payment-management/bulk-update")
    public String bulkUpdatePayments(HttpServletRequest request, HttpSession session, Principal p) {
        try {
            int successCount = 0;
            int totalChanges = 0;
            String teacherEmail = p.getName();
            
            java.util.Map<String, String[]> paramMap = request.getParameterMap();
            java.util.Map<String, java.util.Map<String, String>> changesByPayment = new java.util.HashMap<>();
            
            for (String paramName : paramMap.keySet()) {
                if (paramName.startsWith("changes[")) {
                    String[] values = paramMap.get(paramName);
                    if (values.length > 0) {
                        String indexPart = paramName.substring(8, paramName.indexOf(']'));
                        String fieldPart = paramName.substring(paramName.indexOf('.') + 1);
                        
                        changesByPayment.computeIfAbsent(indexPart, k -> new java.util.HashMap<>()).put(fieldPart, values[0]);
                    }
                }
            }
            
            for (java.util.Map<String, String> change : changesByPayment.values()) {
                String paymentId = change.get("paymentId");
                String field = change.get("field");
                String value = change.get("value");
                
                if (paymentId != null && field != null && value != null && field.equals("status")) {
                    totalChanges++;
                    
                    try {
                        Long id = Long.parseLong(paymentId);
                        Payment.PaymentStatus status = Payment.PaymentStatus.valueOf(value);
                        System.out.println("Updating payment " + id + " to status: " + status);
                        ((com.m4nas.service.PaymentServiceImpl) paymentService).updatePaymentStatus(id, status, teacherEmail);
                        successCount++;
                        System.out.println("Successfully updated payment " + id);
                    } catch (Exception e) {
                        System.err.println("Error updating payment " + paymentId + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            if (totalChanges == 0) {
                session.setAttribute("msg", "No changes to save!");
                session.setAttribute("msgType", "info");
            } else if (successCount == totalChanges) {
                session.setAttribute("msg", "All " + successCount + " payment status changes saved successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", successCount + " out of " + totalChanges + " changes saved successfully.");
                session.setAttribute("msgType", "warning");
            }
            
        } catch (Exception e) {
            session.setAttribute("msg", "Error saving changes: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        
        return "redirect:/teacher/payment-management";
    }

}
