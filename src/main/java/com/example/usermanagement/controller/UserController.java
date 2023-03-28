package com.example.usermanagement.controller;

import com.example.usermanagement.DTO.UserDto;
import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/v1")
public class UserController {
    @Autowired
    UserService service;

    //Registration of User on the Portal
    @PostMapping("/user-registration")
    public UserDto addUser(@RequestBody User user) {
        log.info("Inside addUser Method of UserController class.");
        return service.createUser(user);
    }

    //Get User by ID
    @GetMapping("/byId/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.info("Inside getUsrById method of UserController class.");
        return service.getUserById(id);
    }

    // == Get Users by Paging ==
    @GetMapping("/AllUsersByPage")
    public Page<UserDto> getAllUsersByPage(Pageable pageable) {
        log.info("Inside getAllUsersByPage method of UserController class.");
        return service.getAllUsersByPage(pageable);
    }

    // == Get users by Paging and Sorting ==
    @GetMapping("/AllUsersByPagingAndSorting")
    public Page<UserDto> getAllUsersByPagingAndSorting(@RequestParam Optional<Integer> page,
                                                       @RequestParam Optional<Integer> size,
                                                       @RequestParam Optional<String> sortBy) {
        log.info("Inside UserController method getAllUsersByPagingAndSorting");
        return service.getAllUsersByPage(PageRequest.of(page.orElse(0),  size.orElse(1), Sort.Direction.ASC, sortBy.orElse("userId")));
    }

    @DeleteMapping("/removeUser/{id}")
    public ResponseEntity<UserDto> removeUser(@PathVariable int id){
        log.info("Inside removeUser method of UserController class.");
        return service.removeUser(id);
    }

    // == Updating User ==
    @PutMapping("/updateUser/{id}")
    public UserDto updateUser(@PathVariable int id, @RequestBody User user) {
        log.info("Inside updateUser method of UserController class.");
        return service.updateUser(id, user);
    }

    // == Create Address ==
    @PatchMapping("/addAddress/{id}")
    public UserDto addAddressToUse(@PathVariable int id, @RequestBody Address address) {
        log.info("Inside addAddressToUser method of UserController class.");
        return service.addAddressToUser(id, address);
    }


}
