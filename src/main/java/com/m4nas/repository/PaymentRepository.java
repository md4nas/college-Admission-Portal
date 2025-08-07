package com.m4nas.repository;

import com.m4nas.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserEmailOrderBySubmissionDateDesc(String userEmail);
    
    Optional<Payment> findByUserEmailAndStatus(String userEmail, Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p ORDER BY p.submissionDate DESC")
    List<Payment> findAllOrderBySubmissionDateDesc();
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    boolean existsByUserEmailAndStatus(String userEmail, Payment.PaymentStatus status);
}