package com.encasa.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professionals")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;
    private String service;
    private String serviceId;
    private Double rating;
    private Integer reviewCount;
    private Integer hourlyRate;
    private String image;
    private String location;

    @Column(length = 1000)
    private String description;

    private String experience;   // "0-2" | "2-5" | "5-10" | "10+"
    private Boolean verified;
    private String availability;

    @ElementCollection
    @CollectionTable(name = "professional_tags", joinColumns = @JoinColumn(name = "professional_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    public Professional() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getService() { return service; }
    public String getServiceId() { return serviceId; }
    public Double getRating() { return rating; }
    public Integer getReviewCount() { return reviewCount; }
    public Integer getHourlyRate() { return hourlyRate; }
    public String getImage() { return image; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getExperience() { return experience; }
    public Boolean getVerified() { return verified; }
    public String getAvailability() { return availability; }
    public List<String> getTags() { return tags; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setService(String service) { this.service = service; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public void setRating(Double rating) { this.rating = rating; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public void setHourlyRate(Integer hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setImage(String image) { this.image = image; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public void setAvailability(String availability) { this.availability = availability; }
    public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }
}
