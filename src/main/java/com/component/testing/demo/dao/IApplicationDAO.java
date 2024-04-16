package com.component.testing.demo.dao;

import com.component.testing.demo.entity.Application;

import java.util.List;

public interface IApplicationDAO {
    void addApplication(Application application);
    boolean applicationExists(String name, String owner);
    Application getApplicationById(int applicationId);
    void updateApplication(Application application);
    void deleteApplication(int applicationId);
    List<Application> getAll();
}
