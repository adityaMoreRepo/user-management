package com.example.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Address {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private int AddressId;
    @Column(
            nullable = false
    )
    private String addressType;
    @Column(
            nullable = false
    )
    private int houseNo;
    private String street;
    private String Locality;
    private String city;
    private String state;
    private String country;
    @Column(
            nullable = false
    )
    private String pincode;

}
