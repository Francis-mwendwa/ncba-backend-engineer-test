package com.ncba.backend_engineer_test.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PPP_PARTNER_MASTER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTNER_ID")
    private Long partnerId;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "MERCHANT_CODE")
    private String merchantCode;

    @Column(name = "PARTNER_NAME")
    private String partnerName;

    @Column(name = "CALLBACK_URL")
    private String callbackUrl;

    @Column(name = "CONTACT_EMAIL")
    private String contactEmail;

    @Column(name = "CONTACT_PHONE")
    private String contactPhone;

    @Column(name = "PARTNER_TYPE")
    private String partnerType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}