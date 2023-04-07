package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private GroupUserDetailsService groupUserDetailsService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testLoadUserByUsername() {
        //given
        User user = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .active(true)
                .roles("ROLE_USER")
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();

        when(userRepository.findByUserName("adi")).thenReturn(Optional.of(user));

        //when
        GroupUserDetails userDetails = (GroupUserDetails) groupUserDetailsService.loadUserByUsername("adi");

        //then
        Assertions.assertEquals(user.getUserName(), userDetails.getUsername());
        Assertions.assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        //given
        when(userRepository.findByUserName("adi")).thenReturn(Optional.empty());

        //when and then
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            groupUserDetailsService.loadUserByUsername("adi");
        });
    }

}
