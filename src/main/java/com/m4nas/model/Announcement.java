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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "creator_role")
    private String creatorRole;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "event_time")
    private String eventTime;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "announcement_type")
    private String announcementType;

    // Constructors
    public Announcement() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.creatorRole = "TEACHER";
        this.announcementType = "GENERAL";
    }

    public Announcement(String title, String content, String createdBy, String targetAudience) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.targetAudience = targetAudience;
        this.announcementType = "GENERAL";
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.creatorRole = "TEACHER";
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatorRole() { return creatorRole; }
    public void setCreatorRole(String creatorRole) { this.creatorRole = creatorRole; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getAnnouncementType() { return announcementType; }
    public void setAnnouncementType(String announcementType) { this.announcementType = announcementType; }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.active == null) {
            this.active = true;
        }
        if (this.announcementType == null) {
            this.announcementType = "GENERAL";
        }
        if (this.creatorRole == null) {
            this.creatorRole = "TEACHER";
        }
    }
}