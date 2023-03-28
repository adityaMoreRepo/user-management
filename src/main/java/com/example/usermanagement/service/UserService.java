package com.example.usermanagement.service;

import com.example.usermanagement.DTO.UserDto;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository repo;


    // == Constructors ==
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public UserService() {
    }


    //== Service Methods ==
    public UserDto createUser(User user) {
        log.info("Inside createUser method of UserService class.");
        User user1 = repo.save(user);
        return convertEntityToDto(user1);
    }


    public UserDto getUserById(int id) {
        log.info("Inside getUserById method of UserService class.");
        Optional<User> opt = repo.findById(id);
//        Assert.notNull(opt, "User ID " + id + " is not Correct.");
        if(opt.get() == null){
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

    private UserDto convertEntityToDto(User user) {
        log.info("Inside convertEntityToDto method of UserService class.");
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setEmailId(user.getEmailId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMobileNo(user.getMobileNo());
        userDto.setAddresses(user.getAddresses());
        return userDto;
    }

    public ResponseEntity<UserDto> removeUser(int id) {
        log.info("Inside removeUser method of UserService class.");
        Optional<User> optionalUser = repo.findById(id);
        optionalUser.ifPresent(user -> repo.delete(user));
        return ResponseEntity.ok().build();
    }

    public UserDto updateUser(int id, User user) {
        log.info("Inside updateUser method of UserService class. ");
        Optional<User> userOptional = repo.findById(id);
        userOptional.ifPresent(user1 -> {
            user1.setEmailId(user.getEmailId());
            user1.setAddresses(user.getAddresses());
            user1.setFirstName(user.getFirstName());
            user1.setLastName(user.getLastName());
            user1.setMobileNo(user.getMobileNo());
            repo.save(user1);
        });
        return convertEntityToDto(userOptional.get());
    }
}
