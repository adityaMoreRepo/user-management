package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// UserDetailService is a default spring security Interface which will help
//us to map User to UserDetails using method loadUserByUsername
//Hence it is DAO for Spring security to access DB for Authentication
@Service
public class GroupUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = repo.findByUserName(username);
        // Mapping User object to GroupUserDetails
        return optionalUser.map(GroupUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + "does not exist."));
    }
}
