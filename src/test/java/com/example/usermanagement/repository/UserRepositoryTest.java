package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    @Autowired
    UserRepository repo;

    @Test
    @Order(1)
    void testUserRepository() {
        long countBefore = repo.count();
        Address address = Address.builder()
                .addressType("Permanent")
                .houseNo(12)
                .street("MG Road")
                .city("Nashik")
                .state("Maharashtra")
                .pincode("421041")
                .country("India")
                .build();
        ArrayList<Address> addressList = new ArrayList<>();
        addressList.add(address);
        User user = User.builder()
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .addresses(addressList)
                .build();
        repo.save(user);
        long countAfter = repo.count();
        assertThat(countAfter).isGreaterThan(countBefore);
    }

    @Test
    @Order(2)
    public void testCreateUser(){
        Address address = Address.builder()
                .addressType("Permanent")
                .houseNo(12)
                .street("MG Road")
                .city("Nashik")
                .state("Maharashtra")
                .pincode("421041")
                .country("India")
                .build();
        ArrayList<Address> addressList = new ArrayList<>();
        addressList.add(address);
        User user = User.builder()
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .addresses(addressList)
                .build();
        User fetchedUser = repo.save(user);
        assertThat(fetchedUser.getUserId()).isNotNull();
    }



}