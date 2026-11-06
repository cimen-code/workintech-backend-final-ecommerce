package com.workintech.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Giris JWT ile yapildigi icin Spring'in varsayilan kullanici/sifre uretimi kapali.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
