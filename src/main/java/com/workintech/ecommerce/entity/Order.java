package com.workintech.ecommerce.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "card_no", nullable = false)
    private Long cardNo;

    @Column(name = "card_name", nullable = false)
    private String cardName;

    @Column(name = "card_expire_month", nullable = false)
    private Integer cardExpireMonth;

    @Column(name = "card_expire_year", nullable = false)
    private Integer cardExpireYear;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(Long userId, Long addressId, LocalDateTime orderDate, Long cardNo, String cardName,
                 Integer cardExpireMonth, Integer cardExpireYear, BigDecimal price) {
        this.userId = userId;
        this.addressId = addressId;
        this.orderDate = orderDate;
        this.cardNo = cardNo;
        this.cardName = cardName;
        this.cardExpireMonth = cardExpireMonth;
        this.cardExpireYear = cardExpireYear;
        this.price = price;
    }

    public void addItem(Product product, Integer count, String detail) {
        items.add(new OrderItem(this, product, count, detail, product.getPrice()));
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public String getCardName() {
        return cardName;
    }

    public Integer getCardExpireMonth() {
        return cardExpireMonth;
    }

    public Integer getCardExpireYear() {
        return cardExpireYear;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
