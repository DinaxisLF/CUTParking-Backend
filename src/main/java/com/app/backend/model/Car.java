package com.app.backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "user_cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private int id;

    @Column(name = "car_plates", unique = true, nullable = false)
    private String carPlates;

    @Column(name = "model", nullable = false)
    private String model;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private User owner;

    @Column(name = "image_url")
    private String imageUrl;


    public Car() {
    }

    // Getters and Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarPlates() {
        return carPlates;
    }

    public void setCarPlates(String carPlates) {
        this.carPlates = carPlates;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
