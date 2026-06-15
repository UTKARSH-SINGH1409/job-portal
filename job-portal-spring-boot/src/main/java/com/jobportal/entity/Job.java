package com.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("_id")
    private Long id;

    @NotBlank(message = "Please provide a title.")
    @Size(min = 3, max = 30, message = "Title must contain between 3 and 30 Characters!")
    private String title;

    @NotBlank(message = "Please provide description.")
    @Size(min = 30, max = 500, message = "Description must contain between 30 and 500 Characters!")
    private String description;

    @NotBlank(message = "Please provide a category.")
    private String category;

    @NotBlank(message = "Please provide a country name.")
    private String country;

    @NotBlank(message = "Please provide a city name.")
    private String city;

    @NotBlank(message = "Please provide location.")
    @Size(min = 20, message = "Location must contain at least 20 characters!")
    private String location;

    private Integer fixedSalary;
    private Integer salaryFrom;
    private Integer salaryTo;

    private Boolean expired = false;

    @CreationTimestamp
    private LocalDateTime jobPostedOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "posted_by_id", referencedColumnName = "id")
    private User postedBy;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Integer getFixedSalary() { return fixedSalary; }
    public void setFixedSalary(Integer fixedSalary) { this.fixedSalary = fixedSalary; }
    
    public Integer getSalaryFrom() { return salaryFrom; }
    public void setSalaryFrom(Integer salaryFrom) { this.salaryFrom = salaryFrom; }
    
    public Integer getSalaryTo() { return salaryTo; }
    public void setSalaryTo(Integer salaryTo) { this.salaryTo = salaryTo; }
    
    public Boolean getExpired() { return expired; }
    public void setExpired(Boolean expired) { this.expired = expired; }
    
    public LocalDateTime getJobPostedOn() { return jobPostedOn; }
    public void setJobPostedOn(LocalDateTime jobPostedOn) { this.jobPostedOn = jobPostedOn; }
    
    public User getPostedBy() { return postedBy; }
    public void setPostedBy(User postedBy) { this.postedBy = postedBy; }
}
