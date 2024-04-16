package com.component.testing.demo.dao;

import com.component.testing.demo.entity.SoftwareRelease;

public interface ISoftwareReleaseDAO {
    void addRelease(SoftwareRelease softwareRelease);
    void addApplication(Integer appId, Integer releaseId);
    SoftwareRelease getReleaseById(int releaseId);
}
