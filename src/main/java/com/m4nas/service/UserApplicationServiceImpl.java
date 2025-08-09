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
public class UserApplicationServiceImpl implements UserApplicationService{

    @Autowired
    private UserApplicationRepository userApplicationRepo;

    @Autowired
    private com.m4nas.repository.UserRepository userRepository;

    @Override
    public UserApplication savePersonalInfo(UserApplication application) {
        if(application.getId() == null){
            application.setId(RandomString.generateUserId());
            application.setStatus("PENDING");
        }
        return userApplicationRepo.save(application);
    }

    @Override
    public UserApplication saveAcademicInfo(UserApplication application) {
        if(application.getId() == null || application.getId().isEmpty()){
            String userEmail = application.getUserEmail();
            if(userEmail != null) {
                com.m4nas.model.UserDtls user = userRepository.findByEmail(userEmail);
                if(user != null) {
                    application.setId(user.getId());
                } else {
                    application.setId(RandomString.generateUserId());
                }
            }
        }

        calculatePercentages(application);
        application.setStatus("SUBMITTED");
        return userApplicationRepo.save(application);
    }

    @Override
    public UserApplication getUserApplicationByEmail(String email) {
        UserApplication app = userApplicationRepo.findByUserEmail(email);
        if(app == null) {
            app = userApplicationRepo.findByUserEmailWithAllFields(email);
        }
        if(app == null) {
            com.m4nas.model.UserDtls user = userRepository.findByEmail(email);
            if(user != null) {
                app = userApplicationRepo.findById(user.getId()).orElse(null);
            }
        }
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
        return null;
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
        boolean existsByEmail = userApplicationRepo.existsByUserEmail(email);
        if(!existsByEmail) {
            com.m4nas.model.UserDtls user = userRepository.findByEmail(email);
            if(user != null) {
                return userApplicationRepo.existsById(user.getId());
            }
        }
        return existsByEmail;
    }

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

    @Override
    public List<UserApplication> getAllApplicationsForAdmin() {
        try {
            return userApplicationRepo.findAll();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getApplicationStatusCounts() {
        try {
            return userApplicationRepo.getApplicationStatusCounts();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getBranchWiseStatistics() {
        try {
            return userApplicationRepo.getBranchWiseStatistics();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<UserApplication> getApplicationsByStatus(String status) {
        try {
            return userApplicationRepo.findByStatus(status);
        } catch (Exception e) {
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
            return null;
        }
    }

    @Override
    public UserApplication updateApplicationCourse(String applicationId, String course) {
        try {
            UserApplication application = userApplicationRepo.findById(applicationId).orElse(null);
            if (application != null) {
                application.setCourse(course);
                return userApplicationRepo.save(application);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserApplication updateApplicationBranch(String applicationId, String allocatedBranch) {
        try {
            UserApplication application = userApplicationRepo.findById(applicationId).orElse(null);
            if (application != null) {
                application.setAllocatedBranch(allocatedBranch);
                if (allocatedBranch != null && !allocatedBranch.isEmpty()) {
                    application.setStatus("ALLOCATED");
                }
                return userApplicationRepo.save(application);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteApplication(Long applicationId) {
        try {
            if (userApplicationRepo.existsById(String.valueOf(applicationId))) {
                userApplicationRepo.deleteById(String.valueOf(applicationId));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete application", e);
        }
    }

    @Override
    public void deleteApplicationsByUserEmail(String userEmail) {
        try {
            UserApplication application = userApplicationRepo.findByUserEmail(userEmail);
            if (application != null) {
                userApplicationRepo.delete(application);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete applications for user", e);
        }
    }

    @Override
    public UserApplication updateApplicationStatus(Long applicationId, String status) {
        try {
            UserApplication application = userApplicationRepo.findById(String.valueOf(applicationId)).orElse(null);
            if (application != null) {
                application.setStatus(status);
                return userApplicationRepo.save(application);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserApplication getApplicationById(String applicationId) {
        return userApplicationRepo.findById(applicationId).orElse(null);
    }

    @Override
    public void calculatePercentages(UserApplication application) {
        if (application.getTotal10Marks() != null && application.getObtain10Marks() != null
                && application.getTotal10Marks() > 0 ) {
            double percentage10 = (application.getObtain10Marks().doubleValue() /
                    application.getTotal10Marks().doubleValue()) * 100;
            application.setPercentage10(Math.round(percentage10 * 100.0) / 100.0);
        }

        if (application.getTotal12Marks() != null && application.getObtain12Marks() != null
                && application.getTotal12Marks() > 0) {
            double percentage12 = (application.getObtain12Marks().doubleValue() /
                    application.getTotal12Marks().doubleValue()) * 100;
            application.setPercentage12(Math.round(percentage12 * 100.0) / 100.0);
        }
    }
}