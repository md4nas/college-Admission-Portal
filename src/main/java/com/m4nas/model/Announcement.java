package com.m4nas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "target_audience")
    private String targetAudience; // STUDENT, ADMIN, ALL

    @Column(name = "announcement_type")
    private String announcementType; // GENERAL, EVENT, DEADLINE, HOLIDAY

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "event_time")
    private String eventTime;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public Announcement() {}

    public Announcement(String title, String content, String createdBy, String targetAudience) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.targetAudience = targetAudience;
        this.announcementType = "GENERAL";
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getAnnouncementType() { return announcementType; }
    public void setAnnouncementType(String announcementType) { this.announcementType = announcementType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}