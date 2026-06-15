package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.security.UserDetailsImpl;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @GetMapping("/getall")
    public ResponseEntity<?> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(Map.of("success", true, "jobs", jobs));
    }

    @PostMapping("/post")
    public ResponseEntity<?> postJob(@Valid @RequestBody Job job, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Job Seeker".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Job Seeker is not allowed to access this resources!"));
        }

        job.setPostedBy(user);
        Job savedJob = jobService.createJob(job);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Job posted successfully!",
            "job", savedJob
        ));
    }

    @GetMapping("/getmyjobs")
    public ResponseEntity<?> getMyJobs(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Job Seeker".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Job Seeker is not allowed to access this resources!"));
        }

        List<Job> myJobs = jobService.getMyJobs(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "myJobs", myJobs));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job updatedJob, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Job Seeker".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Job Seeker is not allowed to access this resources!"));
        }

        Optional<Job> optionalJob = jobService.getJobById(id);
        if (optionalJob.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Job not found!"));
        }

        Job job = optionalJob.get();
        // Here we could map fields from updatedJob to job, or simply reset the ID and user
        updatedJob.setId(id);
        updatedJob.setPostedBy(user);
        Job savedJob = jobService.updateJob(id, updatedJob);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Job Updated Successfully!",
            "job", savedJob
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).get();

        if ("Job Seeker".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Job Seeker is not allowed to access this resources!"));
        }

        Optional<Job> optionalJob = jobService.getJobById(id);
        if (optionalJob.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Job not found!"));
        }

        jobService.deleteJob(id);

        return ResponseEntity.ok(Map.of("success", true, "message", "Job Deleted Successfully!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        Optional<Job> optionalJob = jobService.getJobById(id);
        if (optionalJob.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Job not found!"));
        }

        return ResponseEntity.ok(Map.of("success", true, "job", optionalJob.get()));
    }
}
