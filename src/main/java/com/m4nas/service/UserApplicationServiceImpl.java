package com.m4nas.service;

import com.m4nas.model.UserApplication;
import com.m4nas.repository.UserApplicationRepository;
import com.m4nas.util.RandomString;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class UserApplicationServiceImpl implements  UserApplicationService{

    @Autowired
    private UserApplicationRepository userApplicationRepo;
    
    @Autowired
    private com.m4nas.repository.UserRepository userRepository;

    @Override
    public UserApplication savePersonalInfo(UserApplication application) {
        // Generate ID if new application
        if(application.getId() == null){
            application.setId(RandomString.generateUserId());
            application.setStatus("PENDING");
        }
        return userApplicationRepo.save(application);
    }

    @Override
    public UserApplication saveAcademicInfo(UserApplication application) {
        // Use user ID as application ID for better tracking
        if(application.getId() == null || application.getId().isEmpty()){
            String userEmail = application.getUserEmail();
            if(userEmail != null) {
                // Get user by email and use their ID as application ID
                com.m4nas.model.UserDtls user = userRepository.findByEmail(userEmail);
                if(user != null) {
                    application.setId(user.getId());
                } else {
                    application.setId(RandomString.generateUserId());
                }
            }
        }
        
        //calculate percentage automatically
        calculatePercentages(application);

        //change status to SUBMITTED when academic info is completed
        application.setStatus("SUBMITTED");
        
        // Debug: Print application data before saving
        System.out.println("=== BEFORE SAVING APPLICATION ===");
        System.out.println("ID: " + application.getId());
        System.out.println("Email: " + application.getUserEmail());
        System.out.println("Course: " + application.getCourse());
        System.out.println("Branch1: " + application.getBranch1());
        System.out.println("DOB: " + application.getDob());
        System.out.println("Gender: " + application.getGender());
        System.out.println("Phone: " + application.getPhoneNo());
        System.out.println("Class12 Physics: " + application.getClass12Physics());
        System.out.println("Obtain12Marks: " + application.getObtain12Marks());
        
        UserApplication savedApp = userApplicationRepo.save(application);
        
        // Debug: Print saved application data
        System.out.println("=== AFTER SAVING APPLICATION ===");
        if(savedApp != null) {
            System.out.println("Saved ID: " + savedApp.getId());
            System.out.println("Saved Course: " + savedApp.getCourse());
            System.out.println("Saved Branch1: " + savedApp.getBranch1());
            System.out.println("Saved DOB: " + savedApp.getDob());
        }
        
        return savedApp;
    }

    @Override
    public UserApplication getUserApplicationByEmail(String email) {
        System.out.println("=== SERVICE DEBUG ===");
        System.out.println("Looking for application with email: " + email);
        
        // First try to find by email
        UserApplication app = userApplicationRepo.findByUserEmail(email);
        System.out.println("First lookup by email result: " + (app != null ? "FOUND" : "NOT FOUND"));
        
        // Try with explicit query
        if(app == null) {
            System.out.println("Trying explicit query...");
            app = userApplicationRepo.findByUserEmailWithAllFields(email);
            System.out.println("Explicit query result: " + (app != null ? "FOUND" : "NOT FOUND"));
        }
        
        // If not found by email, try to find by user ID
        if(app == null) {
            com.m4nas.model.UserDtls user = userRepository.findByEmail(email);
            if(user != null) {
                System.out.println("Trying to find application by user ID: " + user.getId());
                app = userApplicationRepo.findById(user.getId()).orElse(null);
                System.out.println("User ID lookup result: " + (app != null ? "FOUND" : "NOT FOUND"));
            }
        }
        
        // If found, print all field values for debugging
        if(app != null) {
            System.out.println("=== APPLICATION FIELD DEBUG ===");
            System.out.println("Course: " + app.getCourse());
            System.out.println("Branch1: " + app.getBranch1());
            System.out.println("DOB: " + app.getDob());
            System.out.println("Gender: " + app.getGender());
            System.out.println("Phone: " + app.getPhoneNo());
            System.out.println("Address: " + app.getAddress());
            System.out.println("Class12 Physics: " + app.getClass12Physics());
            System.out.println("Obtain12Marks: " + app.getObtain12Marks());
            System.out.println("Percentage12: " + app.getPercentage12());
        }
        
        System.out.println("Found application: " + (app != null ? "YES - ID: " + app.getId() + ", Email: " + app.getUserEmail() + ", Status: " + app.getStatus() : "NO"));
        return app;
    }

    @Override
    public UserApplication acceptSeat(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "ALLOCATED".equals(app.getStatus())){
            app.setSeatAccepted(true);
            app.setStatus("ACCEPTED");
            return userApplicationRepo.save(app);
        }
        return null; // Fixed: return null if app is null or wrong status
    }

    @Override
    public UserApplication declineSeat(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "ALLOCATED".equals(app.getStatus())){
            app.setSeatAccepted(false);
            app.setStatus("DECLINED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    @Override
    public boolean hasUserSubmittedApplication(String email) {
        // Check by email first
        boolean existsByEmail = userApplicationRepo.existsByUserEmail(email);
        
        // If not found by email, check by user ID
        if(!existsByEmail) {
            com.m4nas.model.UserDtls user = userRepository.findByEmail(email);
            if(user != null) {
                return userApplicationRepo.existsById(user.getId());
            }
        }
        
        return existsByEmail;
    }

    // ==== TEACHER OPERATION ====

    @Override
    public List<UserApplication> getApplicationsPendingApproval() {
        return userApplicationRepo.findApplicationsPendingApproval();
    }

    @Override
    public List<UserApplication> getApprovedApplicationsForSeatAllocation() {
        return userApplicationRepo.findApprovedApplicationsForSeatAllocation();
    }

    @Override
    public List<UserApplication> getAllocatedApplicationsPendingResponse() {
        return userApplicationRepo.findAllocatedApplicationsPendingResponse();
    }

    @Override
    public UserApplication approveApplication(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "SUBMITTED".equals(app.getStatus())){
            app.setStatus("APPROVED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    @Override
    public UserApplication rejectApplication(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "SUBMITTED".equals(app.getStatus())){
            app.setStatus("REJECTED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    @Override
    public UserApplication allocateSeat(String applicationId, String branch) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "APPROVED".equals(app.getStatus())){
            app.setAllocatedBranch(branch);
            app.setStatus("ALLOCATED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    // ===== ADMIN OPERATIONS (READ-ONLY) =====

    @Override
    public List<UserApplication> getAllApplicationsForAdmin() {
        try {
            return userApplicationRepo.findAll();
        } catch (Exception e) {
            System.err.println("Error getting all applications: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getApplicationStatusCounts() {
        try {
            List<Object[]> result = userApplicationRepo.getApplicationStatusCounts();
            if (result == null || result.isEmpty()) {
                // Fallback: create manual count
                List<UserApplication> allApps = userApplicationRepo.findAll();
                java.util.Map<String, Long> statusMap = new java.util.HashMap<>();
                for (UserApplication app : allApps) {
                    statusMap.put(app.getStatus(), statusMap.getOrDefault(app.getStatus(), 0L) + 1);
                }
                List<Object[]> fallbackResult = new java.util.ArrayList<>();
                for (java.util.Map.Entry<String, Long> entry : statusMap.entrySet()) {
                    fallbackResult.add(new Object[]{entry.getKey(), entry.getValue()});
                }
                return fallbackResult;
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error getting status counts: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getBranchWiseStatistics() {
        try {
            List<Object[]> result = userApplicationRepo.getBranchWiseStatistics();
            if (result == null || result.isEmpty()) {
                // Fallback: create manual statistics
                List<UserApplication> allocatedApps = userApplicationRepo.findAll().stream()
                    .filter(app -> app.getAllocatedBranch() != null)
                    .collect(java.util.stream.Collectors.toList());
                
                java.util.Map<String, java.util.List<UserApplication>> branchMap = new java.util.HashMap<>();
                for (UserApplication app : allocatedApps) {
                    branchMap.computeIfAbsent(app.getAllocatedBranch(), k -> new java.util.ArrayList<>()).add(app);
                }
                
                List<Object[]> fallbackResult = new java.util.ArrayList<>();
                for (java.util.Map.Entry<String, java.util.List<UserApplication>> entry : branchMap.entrySet()) {
                    String branch = entry.getKey();
                    List<UserApplication> apps = entry.getValue();
                    long count = apps.size();
                    double avgMarks = apps.stream().mapToInt(UserApplication::getObtain12Marks).average().orElse(0.0);
                    fallbackResult.add(new Object[]{branch, count, avgMarks});
                }
                return fallbackResult;
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error getting branch statistics: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<UserApplication> getApplicationsByStatus(String status) {
        try {
            return userApplicationRepo.findByStatus(status);
        } catch (Exception e) {
            System.err.println("Error getting applications by status: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<UserApplication> getAllApplications() {
        return userApplicationRepo.findAllByOrderBySubmissionDateDesc();
    }
    
    @Override
    public UserApplication updateApplicationStatus(String applicationId, String status) {
        try {
            UserApplication application = userApplicationRepo.findById(applicationId).orElse(null);
            if (application != null) {
                application.setStatus(status);
                return userApplicationRepo.save(application);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error updating application status: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public UserApplication updateApplicationCourse(String applicationId, String course) {
        try {
            UserApplication application = userApplicationRepo.findById(applicationId).orElse(null);
            if (application != null) {
                application.setCourse(course);
                // Auto-update status based on course assignment
                if (course != null && !course.isEmpty() && "PENDING".equals(application.getStatus())) {
                    application.setStatus("REVIEWED");
                }
                return userApplicationRepo.save(application);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error updating application course: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public UserApplication updateApplicationBranch(String applicationId, String allocatedBranch) {
        try {
            UserApplication application = userApplicationRepo.findById(applicationId).orElse(null);
            if (application != null) {
                String oldBranch = application.getAllocatedBranch();
                application.setAllocatedBranch(allocatedBranch);
                
                // Auto-update status based on branch allocation
                if (allocatedBranch != null && !allocatedBranch.isEmpty()) {
                    // Branch allocated - set status to ALLOCATED
                    application.setStatus("ALLOCATED");
                } else if ((oldBranch != null && !oldBranch.isEmpty()) && (allocatedBranch == null || allocatedBranch.isEmpty())) {
                    // Branch removed - revert to previous status
                    if ("ALLOCATED".equals(application.getStatus()) || "ACCEPTED".equals(application.getStatus()) || "DECLINED".equals(application.getStatus())) {
                        application.setStatus("APPROVED");
                    }
                }
                
                return userApplicationRepo.save(application);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error updating application branch: " + e.getMessage());
            return null;
        }
    }

    // ===== UTILITY METHODS =====

    @Override
    public UserApplication getApplicationById(String applicationId) {
        System.out.println("=== SERVICE getApplicationById DEBUG ===");
        System.out.println("Searching for ID: " + applicationId);
        
        // First, let's see all applications in database
        List<UserApplication> allApps = userApplicationRepo.findAll();
        System.out.println("Total applications in database: " + allApps.size());
        for (UserApplication app : allApps) {
            System.out.println("- ID: " + app.getId() + ", Email: " + app.getUserEmail() + ", Status: " + app.getStatus());
        }
        
        UserApplication result = userApplicationRepo.findById(applicationId).orElse(null);
        System.out.println("Result: " + (result != null ? "FOUND" : "NOT FOUND"));
        return result;
    }

    @Override
    public void calculatePercentages(UserApplication application) {
        // Calculate class 10 percentage
        if (application.getTotal10Marks() != null && application.getObtain10Marks() != null
        && application.getTotal10Marks() > 0 ) {
            double percentage10 = (application.getObtain10Marks().doubleValue() /
                    application.getTotal10Marks().doubleValue()) * 100;
            application.setPercentage10(Math.round(percentage10 * 100.0) / 100.0);
        }

        // Calculate Class 12 percentage
        if (application.getTotal12Marks() != null && application.getObtain12Marks() != null
                && application.getTotal12Marks() > 0) {
            double percentage12 = (application.getObtain12Marks().doubleValue() /
                    application.getTotal12Marks().doubleValue()) * 100;
            application.setPercentage12(Math.round(percentage12 * 100.0) / 100.0);
        }

    }
}
