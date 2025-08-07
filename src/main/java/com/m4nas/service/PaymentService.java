package com.m4nas.service;

import com.m4nas.model.Payment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PaymentService {
    
    Payment submitPayment(String userEmail, String studentName, String course, String branch,
                         Double amount, String paymentMethod, String transactionId,
                         MultipartFile receiptFile, String notes);
    
    List<Payment> getPaymentsByUser(String userEmail);
    
    List<Payment> getAllPayments();
    
    Payment verifyPayment(Long paymentId, String verifiedBy);
    
    Payment rejectPayment(Long paymentId, String verifiedBy);
    
    boolean hasVerifiedPayment(String userEmail);
}