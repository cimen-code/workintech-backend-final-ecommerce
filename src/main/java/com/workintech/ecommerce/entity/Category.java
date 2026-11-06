package com.workintech.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String img;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private String gender;

    protected Category() {
    }

    public Category(Long id, String code, String title, String img, Double rating, String gender) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.img = img;
        this.rating = rating;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public Double getRating() {
        return rating;
    }

    public String getGender() {
        return gender;
    }
}
