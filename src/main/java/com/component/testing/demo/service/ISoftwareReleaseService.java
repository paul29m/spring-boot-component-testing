package com.component.testing.demo.service;

import com.component.testing.demo.dto.ReleaseDTO;
import com.component.testing.demo.entity.SoftwareRelease;

public interface ISoftwareReleaseService {
    Integer addRelease(SoftwareRelease softwareRelease);
    void addApplication(Integer appId, Integer releaseId);

    /**
     * Get release information by id
     * @param releaseId - id of the release
     * @return  release information
     */
    ReleaseDTO getReleaseById(Integer releaseId);
}
