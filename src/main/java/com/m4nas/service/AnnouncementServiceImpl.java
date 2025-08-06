package com.m4nas.service;

import com.m4nas.model.Announcement;
import com.m4nas.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public Announcement saveAnnouncement(Announcement announcement) {
        try {
            System.out.println("=== ANNOUNCEMENT SERVICE DEBUG ===");
            System.out.println("Before save - Title: " + announcement.getTitle());
            System.out.println("Before save - Content: " + announcement.getContent());
            System.out.println("Before save - Created By: " + announcement.getCreatedBy());
            System.out.println("Before save - Target Audience: " + announcement.getTargetAudience());
            
            announcement.setCreatedAt(LocalDateTime.now());
            announcement.setActive(true);
            announcement.setCreatorRole("TEACHER");
            
            Announcement savedAnnouncement = announcementRepository.save(announcement);
            System.out.println("After save - ID: " + savedAnnouncement.getId());
            System.out.println("After save - Created At: " + savedAnnouncement.getCreatedAt());
            
            return savedAnnouncement;
        } catch (Exception e) {
            System.err.println("Error in saveAnnouncement service: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Announcement> getActiveAnnouncements() {
        List<Announcement> announcements = announcementRepository.findActiveAnnouncements();
        System.out.println("getActiveAnnouncements() returned: " + announcements.size() + " announcements");
        return announcements;
    }

    @Override
    public List<Announcement> getAnnouncementsByAudience(String audience) {
        List<Announcement> announcements = announcementRepository.findActiveAnnouncementsByAudience(audience);
        System.out.println("getAnnouncementsByAudience(" + audience + ") returned: " + announcements.size() + " announcements");
        return announcements;
    }

    @Override
    public List<Announcement> getAnnouncementsByCreator(String createdBy) {
        List<Announcement> announcements = announcementRepository.findByCreatedBy(createdBy);
        System.out.println("getAnnouncementsByCreator(" + createdBy + ") returned: " + announcements.size() + " announcements");
        return announcements;
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}