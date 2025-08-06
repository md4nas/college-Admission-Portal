package com.m4nas;

import com.m4nas.model.Announcement;
import com.m4nas.repository.AnnouncementRepository;
import com.m4nas.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class AnnouncementTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Test
    public void testCreateAnnouncement() {
        // Create test announcement
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Announcement");
        announcement.setContent("This is a test announcement");
        announcement.setCreatedBy("test@teacher.com");
        announcement.setTargetAudience("STUDENT");
        announcement.setAnnouncementType("GENERAL");
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setActive(true);

        // Save announcement
        Announcement saved = announcementService.saveAnnouncement(announcement);
        
        System.out.println("Test - Saved announcement ID: " + saved.getId());
        System.out.println("Test - Saved announcement title: " + saved.getTitle());

        // Verify it was saved
        List<Announcement> all = announcementService.getActiveAnnouncements();
        System.out.println("Test - Total active announcements: " + all.size());

        // Verify by creator
        List<Announcement> byCreator = announcementService.getAnnouncementsByCreator("test@teacher.com");
        System.out.println("Test - Announcements by creator: " + byCreator.size());

        // Verify by audience
        List<Announcement> byAudience = announcementService.getAnnouncementsByAudience("STUDENT");
        System.out.println("Test - Announcements for students: " + byAudience.size());
    }
}