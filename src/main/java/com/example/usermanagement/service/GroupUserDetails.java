package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//We are converting the User Object to UserDetails. As UserDetails is what Spring security understands and internally
// implemented.
public class GroupUserDetails implements UserDetails {
    // == Fields ==
    private String userName;
    private String password;
    private Boolean isActive;
    private List<GrantedAuthority> authorities;  //For user roles details

    // == Constructor ==
    public GroupUserDetails(User user){
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.isActive = user.getActive();
        this.authorities = Arrays.
                stream(user.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
