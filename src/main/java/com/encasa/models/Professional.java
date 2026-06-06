package com.encasa.models;

import jakarta.persistence.*;

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

    private Integer experience;
    private Boolean verified;
    private String availability;

    public Professional() {}

    public Professional(String name, String service, String serviceId, Double rating,
                        Integer reviewCount, Integer hourlyRate, String image, String location,
                        String description, Integer experience, Boolean verified, String availability) {
        this.name = name;
        this.service = service;
        this.serviceId = serviceId;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.hourlyRate = hourlyRate;
        this.image = image;
        this.location = location;
        this.description = description;
        this.experience = experience;
        this.verified = verified;
        this.availability = availability;
    }

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
    public Integer getExperience() { return experience; }
    public Boolean getVerified() { return verified; }
    public String getAvailability() { return availability; }

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
    public void setExperience(Integer experience) { this.experience = experience; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public void setAvailability(String availability) { this.availability = availability; }
}
