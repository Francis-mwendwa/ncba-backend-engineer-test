package com.ncba.backend_engineer_test.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isoCode;
    private String name;
    private String capital;
    private String currency;
    private String continent;
    private String language;
}
