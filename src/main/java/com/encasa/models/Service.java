package com.encasa.models;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Service {

    @Id
    private String id;

    private String name;
    private String description;
    private String icon;
    private String color;

    public Service() {}

    public Service(String id, String name, String description, String icon, String color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
}
