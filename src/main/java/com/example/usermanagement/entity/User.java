package com.example.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_mobile_no", columnNames = "mobileNo"),
                @UniqueConstraint(name = "unique_user_name", columnNames = "userName"),
                @UniqueConstraint(name = "unique_email_id", columnNames = "emailId")}

)
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private int userId;

    @Column(
            nullable = false
    )
    private String userName;

    @Column(
            nullable = false
    )
    private String password;
    private Boolean active;
    private String roles;//ROLE_USER,ROLE_ADMIN etc.
    @Column(
            nullable = false
    )
    private String emailId;
    @Column(
            nullable = false
    )
    private String firstName;
    @Column(
            nullable = false
    )
    private String lastName;
    @Column(
            nullable = false
    )
    private String mobileNo;

    @OneToMany(
            targetEntity = Address.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private List<Address> addresses;
}
