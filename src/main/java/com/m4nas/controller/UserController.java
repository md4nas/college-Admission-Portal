package com.m4nas.controller;

import com.m4nas.model.UserApplication;
import com.m4nas.model.Announcement;
import com.m4nas.service.UserApplicationService;
import com.m4nas.service.AnnouncementService;
import com.m4nas.service.PaymentService;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserApplicationService userApplicationService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private PaymentService paymentService;



    @GetMapping("/")
    public String redirectToHome(Principal p, Model model, HttpServletRequest request){
        if(p != null){
            String email = p.getName();
            UserDtls user = userRepo.findByEmail(email);
            if(user != null) {
                UserApplication application = userApplicationService.getUserApplicationByEmail(email);
                
                model.addAttribute("user", user);
                model.addAttribute("application", application);
                model.addAttribute("currentPath", request.getRequestURI());
                
                // Pass individual fields like application status page
                if(application != null) {
                    model.addAttribute("appStatus", application.getStatus());
                    model.addAttribute("appCourse", application.getCourse());
                    model.addAttribute("appAllocatedBranch", application.getAllocatedBranch());
                    model.addAttribute("appSubmissionDate", application.getSubmissionDate());
                    model.addAttribute("appParentsPhoneNo", application.getParentsPhoneNo());
                }
                return "user/home";
            } else {
                return "redirect:/signin?error";
            }
        }
        return "redirect:/signin";
    }


    @GetMapping("/settings/changePass")
    public String loadChangePassword(HttpSession session) {
        // Clear any existing messages to prevent showing profile update messages
        session.removeAttribute("msg");
        session.removeAttribute("msgType");
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
            System.out.println("User " + email + " already has application with ID: " + existingApp.getId());
            // Redirect to home page where status is shown
            return "redirect:/user/";
        }
        
        // Also check by hasUserSubmittedApplication method
        boolean hasSubmitted = userApplicationService.hasUserSubmittedApplication(email);
        if(hasSubmitted) {
            System.out.println("User " + email + " has already submitted application (found by alternative check)");
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
        UserDtls user = userRepo.findByEmail(email);
        
        application.setUserEmail(email);
        application.setSubmissionDate(LocalDate.now());
        
        // Set application ID to user ID for better tracking
        if(user != null) {
            application.setId(user.getId());
        }

        try{
            // Debug logging
            System.out.println("=== APPLICATION SUBMISSION DEBUG ===");
            System.out.println("User: " + email + ", User ID: " + (user != null ? user.getId() : "null"));
            System.out.println("Application ID set to: " + application.getId());
            System.out.println("Application data: " + application.getCourse() + ", " + application.getBranch1());
            
            UserApplication savedApp = userApplicationService.saveAcademicInfo(application);
            if(savedApp != null){
                System.out.println("Application saved successfully with ID: " + savedApp.getId());
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
@Transactional(readOnly = true)
public String applicationStatus(Principal p, Model model, HttpServletRequest request){
    String email = p.getName();
    UserDtls user = userRepo.findByEmail(email);
    UserApplication application = userApplicationService.getUserApplicationByEmail(email);
    
    // Pass individual fields to avoid Hibernate proxy issues
    if(application != null) {
        model.addAttribute("appCourse", application.getCourse());
        model.addAttribute("appBranch1", application.getBranch1());
        model.addAttribute("appBranch2", application.getBranch2());
        model.addAttribute("appDob", application.getDob());
        model.addAttribute("appGender", application.getGender());
        model.addAttribute("appPhone", application.getPhoneNo());
        model.addAttribute("appAddress", application.getAddress());
        model.addAttribute("appParentsName", application.getParentsName());
        model.addAttribute("appParentsPhoneNo", application.getParentsPhoneNo());
        model.addAttribute("appReligion", application.getReligion());
        model.addAttribute("appCaste", application.getCaste());
        model.addAttribute("appCity", application.getCity());
        model.addAttribute("appState", application.getState());
        model.addAttribute("appPincode", application.getPincode());
        model.addAttribute("appStatus", application.getStatus());
        model.addAttribute("appPercentage12", application.getPercentage12());
        model.addAttribute("appObtain12Marks", application.getObtain12Marks());
        model.addAttribute("appTotal12Marks", application.getTotal12Marks());
        model.addAttribute("appPercentage10", application.getPercentage10());
        model.addAttribute("appObtain10Marks", application.getObtain10Marks());
        model.addAttribute("appTotal10Marks", application.getTotal10Marks());
        model.addAttribute("appSchoolName10", application.getSchoolName10());
        model.addAttribute("appBoard10Name", application.getBoard10Name());
        model.addAttribute("appPassing10Year", application.getPassing10Year());
        model.addAttribute("appRollNo10", application.getRollNo10());
        model.addAttribute("appClass10Math", application.getClass10Math());
        model.addAttribute("appClass10Science", application.getClass10Science());
        model.addAttribute("appClass10English", application.getClass10English());
        model.addAttribute("appClass10Hindi", application.getClass10Hindi());
        model.addAttribute("appClass10Social", application.getClass10Social());
        model.addAttribute("appSchoolName12", application.getSchoolName12());
        model.addAttribute("appBoard12Name", application.getBoard12Name());
        model.addAttribute("appPassing12Year", application.getPassing12Year());
        model.addAttribute("appRollNo12", application.getRollNo12());
        model.addAttribute("appClass12Physics", application.getClass12Physics());
        model.addAttribute("appClass12Chemistry", application.getClass12Chemistry());
        model.addAttribute("appClass12Maths", application.getClass12Maths());
        model.addAttribute("appClass12English", application.getClass12English());
        model.addAttribute("appClass12Optional", application.getClass12Optional());
        model.addAttribute("appAllocatedBranch", application.getAllocatedBranch());
        model.addAttribute("appSeatAccepted", application.getSeatAccepted());
    }
    
    model.addAttribute("user", user);
    model.addAttribute("application", application);
    model.addAttribute("currentPath", request.getRequestURI());
    
    return "user/application_status";
}

    @PostMapping("/application/accept-seat")
    public String acceptSeat(Principal p, HttpSession session, @RequestParam(value = "redirectToPayment", required = false) String redirectToPayment){
        String email = p.getName();
        UserApplication application = userApplicationService.getUserApplicationByEmail(email);

        if(application != null && "ALLOCATED".equals(application.getStatus())){
            UserApplication updatedApplication = userApplicationService.acceptSeat(application.getId());
            if(updatedApplication != null){
                session.setAttribute("msg", "Seat accepted successfully! You can now proceed with fee payment.");
                session.setAttribute("msgType", "success");
                
                // Redirect to payment page if requested
                if("true".equals(redirectToPayment)) {
                    return "redirect:/user/payment";
                }
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

    // ===== NEW USER FEATURES =====

    @GetMapping("/settings/editProfile")
    public String editProfile(Principal p, Model model, HttpServletRequest request) {
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("currentPath", request.getRequestURI());
        return "user/settings/edit_profile";
    }

    @PostMapping("/settings/updateProfile")
    public String updateProfile(@ModelAttribute UserDtls updatedUser, Principal p, HttpSession session) {
        String email = p.getName();
        UserDtls existingUser = userRepo.findByEmail(email);
        
        if(existingUser != null) {
            // Only allow updating full name (email and other fields remain unchanged)
            existingUser.setFullName(updatedUser.getFullName());
            UserDtls savedUser = userRepo.save(existingUser);
            
            if(savedUser != null) {
                session.setAttribute("msg", "Profile updated successfully!");
                session.setAttribute("msgType", "success");
            } else {
                session.setAttribute("msg", "Failed to update profile. Please try again.");
                session.setAttribute("msgType", "danger");
            }
        } else {
            session.setAttribute("msg", "User not found!");
            session.setAttribute("msgType", "danger");
        }
        return "redirect:/user/settings/editProfile";
    }

    @GetMapping("/notifications")
    public String notifications(Principal p, Model model, HttpServletRequest request) {
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);
        UserApplication application = userApplicationService.getUserApplicationByEmail(email);
        
        // Get announcements for students (STUDENT and ALL)
        List<Announcement> announcements = announcementService.getAnnouncementsByAudience("STUDENT");
        
        System.out.println("=== USER NOTIFICATIONS DEBUG ===");
        System.out.println("User: " + email);
        System.out.println("Announcements found: " + announcements.size());
        for (Announcement ann : announcements) {
            System.out.println("- " + ann.getTitle() + " (" + ann.getTargetAudience() + ")");
        }
        
        model.addAttribute("user", user);
        model.addAttribute("application", application);
        model.addAttribute("announcements", announcements);
        model.addAttribute("currentPath", request.getRequestURI());
        return "user/notifications";
    }

    @GetMapping("/courses")
    public String myCourses(Principal p, Model model, HttpServletRequest request) {
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);
        UserApplication application = userApplicationService.getUserApplicationByEmail(email);
        
        model.addAttribute("user", user);
        model.addAttribute("application", application);
        model.addAttribute("currentPath", request.getRequestURI());
        return "user/my_courses";
    }

    @GetMapping("/payment")
    public String paymentStatus(Principal p, Model model, HttpServletRequest request) {
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);
        UserApplication application = userApplicationService.getUserApplicationByEmail(email);
        
        // Pass individual fields like application status page
        if(application != null) {
            model.addAttribute("appStatus", application.getStatus());
            model.addAttribute("appCourse", application.getCourse());
            model.addAttribute("appAllocatedBranch", application.getAllocatedBranch());
            model.addAttribute("appSeatAccepted", application.getSeatAccepted());
        }
        
        model.addAttribute("user", user);
        model.addAttribute("application", application);
        model.addAttribute("currentPath", request.getRequestURI());
        return "user/payment_status";
    }
    
    @PostMapping("/payment/submit")
    public String submitPayment(@RequestParam("studentName") String studentName,
                               @RequestParam("courseInfo") String courseInfo,
                               @RequestParam("amountPaid") Double amountPaid,
                               @RequestParam("paymentMethod") String paymentMethod,
                               @RequestParam("transactionId") String transactionId,
                               @RequestParam(value = "receiptFile", required = false) MultipartFile receiptFile,
                               @RequestParam(value = "paymentNotes", required = false) String paymentNotes,
                               Principal p, HttpSession session) {
        
        try {
            String email = p.getName();
            UserApplication application = userApplicationService.getUserApplicationByEmail(email);
            
            if (application == null) {
                session.setAttribute("msg", "No application found!");
                session.setAttribute("msgType", "danger");
                return "redirect:/user/payment";
            }
            
            // Extract course and branch from courseInfo or use application data
            String course = application.getCourse() != null ? application.getCourse() : "N/A";
            String branch = application.getAllocatedBranch() != null ? application.getAllocatedBranch() : "N/A";
            
            paymentService.submitPayment(email, studentName, course, branch, amountPaid, 
                                       paymentMethod, transactionId, receiptFile, paymentNotes);
            
            session.setAttribute("msg", "Payment submitted successfully! Your payment will be verified within 2-3 working days.");
            session.setAttribute("msgType", "success");
            
        } catch (Exception e) {
            session.setAttribute("msg", "Error submitting payment: " + e.getMessage());
            session.setAttribute("msgType", "danger");
        }
        
        return "redirect:/user/payment";
    }
}
