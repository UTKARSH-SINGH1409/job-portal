package com.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("_id")
    private Long id;

    @NotBlank(message = "Please enter your Name!")
    @Size(min = 3, max = 30, message = "Name must contain between 3 and 30 Characters!")
    private String name;

    @NotBlank(message = "Please enter your Email!")
    @Email(message = "Please provide a valid Email!")
    private String email;

    @NotBlank(message = "Please provide a cover letter!")
    private String coverLetter;

    @NotNull(message = "Please enter your Phone Number!")
    private Long phone;

    @NotBlank(message = "Please enter your Address!")
    private String address;

    // Resume Embeddable
    @Embedded
    private Resume resume;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    private User applicant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    private User employer;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    
    public Long getPhone() { return phone; }
    public void setPhone(Long phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }
    
    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }
    
    public User getEmployer() { return employer; }
    public void setEmployer(User employer) { this.employer = employer; }
}
