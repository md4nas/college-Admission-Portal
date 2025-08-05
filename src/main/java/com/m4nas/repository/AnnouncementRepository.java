package com.m4nas.repository;

import com.m4nas.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    @Query("SELECT a FROM Announcement a WHERE a.isActive = true ORDER BY a.createdDate DESC")
    List<Announcement> findActiveAnnouncements();
    
    @Query("SELECT a FROM Announcement a WHERE a.isActive = true AND a.targetAudience IN ('ALL', ?1) ORDER BY a.createdDate DESC")
    List<Announcement> findActiveAnnouncementsByAudience(String audience);
    
    @Query("SELECT a FROM Announcement a WHERE a.createdBy = ?1 ORDER BY a.createdDate DESC")
    List<Announcement> findByCreatedBy(String createdBy);
}