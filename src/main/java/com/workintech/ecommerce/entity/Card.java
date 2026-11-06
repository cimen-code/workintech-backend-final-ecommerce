package com.workintech.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "card_no", nullable = false)
    private String cardNo;

    @Column(name = "expire_month", nullable = false)
    private Integer expireMonth;

    @Column(name = "expire_year", nullable = false)
    private Integer expireYear;

    @Column(name = "name_on_card", nullable = false)
    private String nameOnCard;

    protected Card() {
    }

    public Card(Long userId) {
        this.userId = userId;
    }

    public void update(String cardNo, Integer expireMonth, Integer expireYear, String nameOnCard) {
        this.cardNo = cardNo;
        this.expireMonth = expireMonth;
        this.expireYear = expireYear;
        this.nameOnCard = nameOnCard;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public Integer getExpireMonth() {
        return expireMonth;
    }

    public Integer getExpireYear() {
        return expireYear;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }
}
