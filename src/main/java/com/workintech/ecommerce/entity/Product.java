package com.workintech.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "sell_count", nullable = false)
    private Integer sellCount;

    @OneToMany(mappedBy = "product")
    @OrderBy("imageIndex ASC")
    private List<ProductImage> images = new ArrayList<>();

    protected Product() {
    }

    public Product(Long id, String name, String description, BigDecimal price, Integer stock, Long storeId,
                   Category category, Double rating, Integer sellCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.storeId = storeId;
        this.category = category;
        this.rating = rating;
        this.sellCount = sellCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Category getCategory() {
        return category;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getSellCount() {
        return sellCount;
    }

    public List<ProductImage> getImages() {
        return images;
    }
}
