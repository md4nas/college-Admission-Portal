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
        List<UserApplication> allocatedApplications = userApplicationService.getAllocatedApplicationsPendingResponse();
        List<UserApplication> approvedApplications = userApplicationService.getApprovedApplicationsForSeatAllocation();
        List<Object[]> statusCounts = userApplicationService.getApplicationStatusCounts();
        List<Object[]> branchStats = userApplicationService.getBranchWiseStatistics();
        

        
        // Recent Announcements (top 10 for scroll)
        List<Announcement> recentAnnouncements = announcementService.getActiveAnnouncements().stream().limit(10).collect(java.util.stream.Collectors.toList());
        

        
        // Add to model
        model.addAttribute("pendingCount", pendingApplications.size());
        model.addAttribute("approvedCount", approvedApplications.size());
        model.addAttribute("allocatedCount", allocatedApplications.size());
        // Recent Applications (all applications, not just pending)
        List<UserApplication> allApplications = userApplicationService.getAllApplications();
        List<UserApplication> recentApplications = allApplications.stream()
            .sorted((a, b) -> {
                if (a.getSubmissionDate() == null && b.getSubmissionDate() == null) return 0;
                if (a.getSubmissionDate() == null) return 1;
                if (b.getSubmissionDate() == null) return -1;
                return b.getSubmissionDate().compareTo(a.getSubmissionDate());
            })
            .limit(7)
            .collect(java.util.stream.Collectors.toList());
        
        // Create a map of email to student name for recent applications
        java.util.Map<String, String> studentNames = new java.util.HashMap<>();
        for (UserApplication app : recentApplications) {
            if (app.getUserEmail() != null) {
                UserDtls student = userRepo.findByEmail(app.getUserEmail());
                if (student != null) {
                    studentNames.put(app.getUserEmail(), student.getFullName());
                }
            }
        }
        
        model.addAttribute("recentApplications", recentApplications);
        model.addAttribute("studentNames", studentNames);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("branchStats", branchStats);

        model.addAttribute("recentAnnouncements", recentAnnouncements);
        
        return "teacher/home";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model) {
        List<UserApplication> applications = userApplicationService.getAllApplications();
        // Sort by class 12 percentage in descending order
        applications.sort((a, b) -> {
            Double percentageA = a.getPercentage12() != null ? a.getPercentage12() : 0.0;
            Double percentageB = b.getPercentage12() != null ? b.getPercentage12() : 0.0;
            return Double.compare(percentageB, percentageA);
        });
        
        // Create a map of email to student name
        java.util.Map<String, String> studentNames = new java.util.HashMap<>();
        for (UserApplication app : applications) {
            if (app.getUserEmail() != null) {
                UserDtls student = userRepo.findByEmail(app.getUserEmail());
                if (student != null) {
                    studentNames.put(app.getUserEmail(), student.getFullName());
                }
            }
        }
        
        model.addAttribute("applications", applications);
        model.addAttribute("studentNames", studentNames);
        return "teacher/applications";
    }

    @GetMapping("/applications/status")
    public String viewApplicationsByStatus(@RequestParam("status") String status, Model model) {
        List<UserApplication> applications = userApplicationService.getApplicationsByStatus(status);
        applications.sort((a, b) -> {
            Double percentageA = a.getPercentage12() != null ? a.getPercentage12() : 0.0;
            Double percentageB = b.getPercentage12() != null ? b.getPercentage12() : 0.0;
            return Double.compare(percentageB, percentageA);
        });
        
        // Create a map of email to student name
        java.util.Map<String, String> studentNames = new java.util.HashMap<>();
        for (UserApplication app : applications) {
            if (app.getUserEmail() != null) {
                UserDtls student = userRepo.findByEmail(app.getUserEmail());
                if (student != null) {
                    studentNames.put(app.getUserEmail(), student.getFullName());
                }
            }
        }
        
        model.addAttribute("applications", applications);
        model.addAttribute("studentNames", studentNames);
        model.addAttribute("filterStatus", status);
        return "teacher/applications";
    }

    @PostMapping("/applications/update-status")
    public String updateApplicationStatusTeacher(@RequestParam("applicationId") String applicationId,
                                          @RequestParam("newStatus") String newStatus,
                                          HttpSession session) {
        try {
            UserApplication updatedApp = userApplicationService.updateApplicationStatus(applicationId, newStatus);
            if(updatedApp != null) {
                session.setAttribute("msg", "Application status updated successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to update application status.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error updating application status.");
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/applications";
    }

    @PostMapping("/applications/delete/{applicationId}")
    public String deleteApplicationTeacher(@PathVariable("applicationId") String applicationId,
                                    HttpSession session) {
        try {
            userApplicationService.deleteApplication(Long.parseLong(applicationId));
            session.setAttribute("msg", "Application deleted successfully!");
            session.setAttribute("msgType", "success");
        } catch (Exception e) {
            session.setAttribute("msg", "Error deleting application.");
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/applications";
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



    @GetMapping("/application-details/{applicationId}")
    public String viewApplicationDetails(@PathVariable("applicationId") String applicationId, Model model) {
        UserApplication application = userApplicationService.getApplicationById(applicationId);
        
        if (application != null) {
            // Get user details for the application (student details)
            UserDtls studentUser = userRepo.findByEmail(application.getUserEmail());
            model.addAttribute("studentUser", studentUser);
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
            Announcement announcement = new Announcement(title, content, principal.getName(), targetAudience);
            announcement.setAnnouncementType(announcementType);
            
            if (eventDate != null && !eventDate.isEmpty()) {
                announcement.setEventDate(java.time.LocalDate.parse(eventDate));
            }
            if (eventTime != null && !eventTime.isEmpty()) {
                announcement.setEventTime(eventTime);
            }
            
            Announcement savedAnnouncement = announcementService.saveAnnouncement(announcement);
            if (savedAnnouncement != null) {
                session.setAttribute("msg", "Announcement created successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to create announcement.");
                session.setAttribute("msgType", "danger");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error creating announcement: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/teacher/";
    }

    @GetMapping("/announcements")
    public String viewAnnouncements(Model model, Principal principal) {
        List<Announcement> announcements = announcementService.getAnnouncementsByCreator(principal.getName());
        

        
        model.addAttribute("announcements", announcements);
        return "teacher/announcements";
    }

    @GetMapping("/seat-management")
    public String seatManagement(Model model) {
        List<UserApplication> applications = userApplicationService.getAllApplications();
        
        // Create a map of email to student name
        java.util.Map<String, String> studentNames = new java.util.HashMap<>();
        for (UserApplication app : applications) {
            if (app.getUserEmail() != null) {
                UserDtls student = userRepo.findByEmail(app.getUserEmail());
                if (student != null) {
                    studentNames.put(app.getUserEmail(), student.getFullName());
                }
            }
        }
        
        model.addAttribute("applications", applications);
        model.addAttribute("studentNames", studentNames);
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
        
        // Create maps for student names and phone numbers
        java.util.Map<String, String> studentNames = new java.util.HashMap<>();
        java.util.Map<String, String> studentPhones = new java.util.HashMap<>();
        for (Payment payment : payments) {
            if (payment.getUserEmail() != null) {
                UserDtls student = userRepo.findByEmail(payment.getUserEmail());
                if (student != null) {
                    studentNames.put(payment.getUserEmail(), student.getFullName());
                }
                // Get phone from UserApplication if available
                List<UserApplication> allApps = userApplicationService.getAllApplications();
                for (UserApplication app : allApps) {
                    if (app.getUserEmail() != null && app.getUserEmail().equals(payment.getUserEmail()) && app.getPhoneNo() != null) {
                        studentPhones.put(payment.getUserEmail(), app.getPhoneNo());
                        break;
                    }
                }
            }
        }
        
        // Calculate statistics
        long pendingCount = payments.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.PENDING).count();
        long verifiedCount = payments.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.VERIFIED).count();
        long rejectedCount = payments.stream().filter(p -> p.getStatus() == Payment.PaymentStatus.REJECTED).count();
        
        model.addAttribute("payments", payments);
        model.addAttribute("studentNames", studentNames);
        model.addAttribute("studentPhones", studentPhones);
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
                        ((com.m4nas.service.PaymentServiceImpl) paymentService).updatePaymentStatus(id, status, teacherEmail);
                        successCount++;
                    } catch (Exception e) {
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
