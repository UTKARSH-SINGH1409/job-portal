package com.jobportal.controller;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.security.UserDetailsImpl;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.CloudinaryService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/employer/getall")
    public ResponseEntity<?> getEmployerApplications(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Job Seeker".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Job Seeker is not allowed to access this resources!"));
        }

        List<Application> applications = applicationService.getEmployerApplications(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "applications", applications));
    }

    @GetMapping("/jobseeker/getall")
    public ResponseEntity<?> getJobseekerApplications(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Employer".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Employer is not allowed to access this resources!"));
        }

        List<Application> applications = applicationService.getJobseekerApplications(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "applications", applications));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Employer".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Employer is not allowed to access this resources!"));
        }

        Optional<Application> optionalApplication = applicationService.getApplicationById(id);
        if (optionalApplication.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Application not found!"));
        }

        applicationService.deleteApplication(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Application Deleted Successfully!"));
    }

    @PostMapping("/post")
    public ResponseEntity<?> postApplication(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("coverLetter") String coverLetter,
            @RequestParam("phone") Long phone,
            @RequestParam("address") String address,
            @RequestParam("jobId") Long jobId,
            @RequestParam(value = "resume", required = false) MultipartFile resumeFile,
            Authentication authentication) throws IOException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Employer".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Employer is not allowed to access this resources!"));
        }

        if (resumeFile == null || resumeFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Resume file is required!"));
        }

        Optional<Job> optionalJob = jobService.getJobById(jobId);
        if (optionalJob.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Job not found!"));
        }

        Map uploadResult = cloudinaryService.upload(resumeFile);
        Resume resume = new Resume();
        resume.setPublic_id((String) uploadResult.get("public_id"));
        String secureUrl = (String) uploadResult.get("secure_url");
        if (secureUrl != null && secureUrl.toLowerCase().endsWith(".pdf")) {
            secureUrl = secureUrl.substring(0, secureUrl.length() - 4) + ".jpg";
        }
        resume.setUrl(secureUrl);

        Application application = new Application();
        application.setName(name);
        application.setEmail(email);
        application.setCoverLetter(coverLetter);
        application.setPhone(phone);
        application.setAddress(address);
        application.setResume(resume);
        application.setApplicant(user);
        application.setEmployer(optionalJob.get().getPostedBy());

        applicationService.createApplication(application);

        return ResponseEntity.ok(Map.of("success", true, "message", "Application Submitted Successfully!"));
    }
}
