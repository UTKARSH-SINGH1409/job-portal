package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public List<Application> getEmployerApplications(Long employerId) {
        return applicationRepository.findByEmployerId(employerId);
    }

    public List<Application> getJobseekerApplications(Long jobseekerId) {
        return applicationRepository.findByApplicantId(jobseekerId);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    public Application createApplication(Application application) {
        return applicationRepository.save(application);
    }
    
    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }
}
