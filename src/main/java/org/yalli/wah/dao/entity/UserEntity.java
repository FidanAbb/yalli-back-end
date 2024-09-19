package org.yalli.wah.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String country;
    private String accessToken;
    private LocalDateTime tokenExpire;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String otp;
    private LocalDateTime otpExpiration;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean otpVerified;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean emailConfirmed = false;


}