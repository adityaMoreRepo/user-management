package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository repo;
    private User user;

    @BeforeEach
    public void setup(){
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
        user = User.builder()
                .userName("adi")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .addresses(addressList)
                .build();
    }

    // == Junit test for Save User operation
    @Test
    @Order(1)
    @Commit
     void givenUserObject_whenSave_thenReturnSavedUser() {
        // Given - Precondition/setup
        //Already setup in @BeforeEach

        // when - behaviour or action that we are going to test
        User savedUser = repo.save(user);
        //then - then verify the output
        // Note - We are using Assertions from assertj library
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("givenUser_whenFindByUsername_thenReturnFetchedUser")
    public void findByUsernameTest(){
        //given
//        repo.save(user); // No need as @Commit will not roll back saved user.
        //when
        User fetchedUser = repo.findByUserName("adi").get();
        //then
        assertThat(fetchedUser).isNotNull();
        assertThat(fetchedUser.getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    @Order(3)
    @DisplayName("givenUser_whenFindByEmailId_thenReturnFetchedUser")
    public void findByEmailId(){
        //given
        //when
        User fetchedUser = repo.findByEmailId(user.getEmailId()).get();
        //then
        assertThat(fetchedUser).isNotNull();
        assertThat(fetchedUser.getEmailId()).isEqualTo(user.getEmailId());
    }




}