package com.workintech.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(name = "tax_no", nullable = false)
    private String taxNo;

    @Column(name = "bank_account", nullable = false)
    private String bankAccount;

    protected Store() {
    }

    public Store(User user, String name, String phone, String taxNo, String bankAccount) {
        this.user = user;
        this.name = name;
        this.phone = phone;
        this.taxNo = taxNo;
        this.bankAccount = bankAccount;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public String getBankAccount() {
        return bankAccount;
    }
}
