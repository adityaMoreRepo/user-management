package com.example.usermanagement.service;

import com.example.usermanagement.DTO.UserDto;
import com.example.usermanagement.common.UserConstant;
import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // == Constructors ==
    public UserService(UserRepository repo, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserService() {
    }


    //== Service Methods ==
    public UserDto createUser(User user) {
        log.info("Inside createUser method of UserService class.");
        user.setRoles(UserConstant.DEFAULT_ROLE);//ROLE_USER
        String encryptedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPwd);
        User user1 = repo.save(user);
        return convertEntityToDto(user1);
    }

    //If logged-in user is ADMIN then he can give access to -> MODERATOR OR ADMIN
    //If logged-in user is MODERATOR  then he can give access to -> MODERATOR
    public String giveAccessToUser(int id, String userRole, Principal principal) {
        log.info("Inside giveAccessToUser method of UserController");
        User user = repo.findById(id).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        //Check if userRole is Available
        String newRole;
        if (activeRoles.contains(userRole)) {
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole);
            repo.save(user);
            log.info(userRole + " assigned to username: " + user.getUserName());
            return "Hi, " + user.getUserName() + ". New Role assign to you by " + principal.getName();
        }
        log.info(userRole + " is not assigned to username: " + user.getUserName());
        return "Hi, " + principal.getName() + " you don't have permission to assign "+ userRole;
    }

    // == Get logged in user ==
    private User getLoggedInUser(Principal principal) {
        log.info("Inside getLoggedInUser method of UsrService class.");
        return repo.findByUserName(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName() + "does not exist."));
    }

    // == Get roles by Logged in user ==
    private List<String> getRolesByLoggedInUser(Principal principal) {
        log.info("Inside getRolesByLoggedInUser method of UserService class.");
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignedRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());

        if (assignedRoles.contains("ROLE_ADMIN")) {
            return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }

//        if (assignedRoles.contains("ROLE_MODERATOR")) {
//            return Arrays.stream(UserConstant.MODERATOR_ACCESS).collect(Collectors.toList());
//        }
        return Collections.emptyList();
    }

    public UserDto getUserById(int id) {
        log.info("Inside getUserById method of UserService class.");
        Optional<User> opt = repo.findById(id);
//        Assert.notNull(opt, "User ID " + id + " is not Correct.");
        if (opt.get() == null) {
            throw new UserNotFoundException("The User ID " + id + " not found.");
        }
        return convertEntityToDto(opt.get());
    }


    public Page<UserDto> getAllUsersByPage(Pageable pageable) {
        log.info("Inside getAllUsersByPage method of UserService class.");
//        Optional<Integer> page = Optional.ofNullable(1);
//        Optional<Integer> size = Optional.ofNullable(1);
//        Optional<String> sortBy = Optional.ofNullable("userId");
//        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(1), Sort.Direction.ASC, sortBy.orElse("userId"));
        Page<UserDto> dtoPage = repo.findAll(pageable)
                .map(this::convertEntityToDto);
        return dtoPage;
    }


    public ResponseEntity<UserDto> removeUser(int id) {
        log.info("Inside removeUser method of UserService class.");
        Optional<User> optionalUser = repo.findById(id);
        optionalUser.ifPresent(user -> repo.delete(user));
        return ResponseEntity.ok().build();
    }

    public UserDto updateUser(int id, User user) {
        log.info("Inside updateUser method of UserService class.");
        Optional<User> userOptional = repo.findById(id);
        userOptional.ifPresent(user1 -> {
            user1.setEmailId(user.getEmailId());
            user1.setAddresses(user.getAddresses());
            user1.setFirstName(user.getFirstName());
            user1.setLastName(user.getLastName());
            user1.setMobileNo(user.getMobileNo());
            user1.setUserName(user.getUserName());
            user1.setActive(user.getActive());
            String encryptedPwd = passwordEncoder.encode(user.getPassword());
            user1.setPassword(encryptedPwd);
            repo.save(user1);
        });
        return convertEntityToDto(userOptional.get());
    }

    public UserDto addAddressToUser(int id, Address address) {
        log.info("Inside addAddressToUser method of UserService.");
        Optional<User> optionalUser = repo.findById(id);
        optionalUser.ifPresent(user -> {
            List<Address> addresses = user.getAddresses();
            addresses.add(address);
            user.setAddresses(addresses);
            repo.save(user);
        });
        return convertEntityToDto(optionalUser.get());
    }

    private UserDto convertEntityToDto(User user) {
        log.info("Inside convertEntityToDto method of UserService class.");
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setEmailId(user.getEmailId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMobileNo(user.getMobileNo());
        userDto.setAddresses(user.getAddresses());
        userDto.setUserName(user.getUserName());
        userDto.setRoles(user.getRoles());
        userDto.setActive(user.getActive());
        return userDto;
    }

    public String updateCredentials(String password, String oldPassword, Principal principal) {
        log.info("Inside updateCredentials method of UserService class.");
        User loggedInUser = getLoggedInUser(principal);
        if (passwordEncoder.matches(oldPassword, loggedInUser.getPassword())) {
            String encryptedNewPws = passwordEncoder.encode(password);
            loggedInUser.setPassword(encryptedNewPws);
            repo.save(loggedInUser);
            return "You have successfully changed the password";
        }
        return "Old password did not match.";
    }

    public List<UserDto> findAll() {
        log.info("Inside findAll method of UserService class.");
        return repo.findAll()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }
}
