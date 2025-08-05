package com.m4nas.service;

import com.m4nas.model.Announcement;
import java.util.List;

public interface AnnouncementService {
    Announcement saveAnnouncement(Announcement announcement);
    List<Announcement> getActiveAnnouncements();
    List<Announcement> getAnnouncementsByAudience(String audience);
    List<Announcement> getAnnouncementsByCreator(String createdBy);
    void deleteAnnouncement(Long id);
}