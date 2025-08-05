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
        announcement.setCreatedDate(LocalDateTime.now());
        announcement.setIsActive(true);
        return announcementRepository.save(announcement);
    }

    @Override
    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findActiveAnnouncements();
    }

    @Override
    public List<Announcement> getAnnouncementsByAudience(String audience) {
        return announcementRepository.findActiveAnnouncementsByAudience(audience);
    }

    @Override
    public List<Announcement> getAnnouncementsByCreator(String createdBy) {
        return announcementRepository.findByCreatedBy(createdBy);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}