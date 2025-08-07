package com.m4nas.service;

import com.m4nas.model.Payment;
import com.m4nas.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    private final String uploadDir = "uploads/receipts/";
    
    @Override
    public Payment submitPayment(String userEmail, String studentName, String course, String branch,
                                Double amount, String paymentMethod, String transactionId,
                                MultipartFile receiptFile, String notes) {
        
        String receiptFileName = null;
        
        // Handle file upload if provided
        if (receiptFile != null && !receiptFile.isEmpty()) {
            try {
                // Create upload directory if it doesn't exist
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Generate unique filename
                String originalFilename = receiptFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                receiptFileName = UUID.randomUUID().toString() + extension;
                
                // Save file
                Path filePath = uploadPath.resolve(receiptFileName);
                Files.copy(receiptFile.getInputStream(), filePath);
                
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload receipt file", e);
            }
        }
        
        Payment payment = new Payment(userEmail, studentName, course, branch, amount, 
                                    paymentMethod, transactionId, receiptFileName, notes);
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public List<Payment> getPaymentsByUser(String userEmail) {
        return paymentRepository.findByUserEmailOrderBySubmissionDateDesc(userEmail);
    }
    
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllOrderBySubmissionDateDesc();
    }
    
    @Override
    public Payment verifyPayment(Long paymentId, String verifiedBy) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(Payment.PaymentStatus.VERIFIED);
        payment.setVerificationDate(LocalDateTime.now());
        payment.setVerifiedBy(verifiedBy);
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment rejectPayment(Long paymentId, String verifiedBy) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(Payment.PaymentStatus.REJECTED);
        payment.setVerificationDate(LocalDateTime.now());
        payment.setVerifiedBy(verifiedBy);
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public boolean hasVerifiedPayment(String userEmail) {
        return paymentRepository.existsByUserEmailAndStatus(userEmail, Payment.PaymentStatus.VERIFIED);
    }
}