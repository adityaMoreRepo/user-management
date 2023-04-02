package com.example.usermanagement.controller;

import com.example.usermanagement.DTO.UserDto;
import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/v1")
public class UserController {
    @Autowired
    UserService service;
    // == Registration of User on the Portal ==
    @PostMapping("/user/registration")
    public UserDto addUser(@RequestBody User user) {
        log.info("Inside addUser Method of UserController class.");
        return service.createUser(user);
    }

    @GetMapping("/access/{id}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String giveAccessToUser(@PathVariable int id, @PathVariable String userRole, Principal principal){
        return service.giveAccessToUser(id, userRole, principal);
    }

    // == Get User by ID ==
    @GetMapping("/byId/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserDto getUserById(@PathVariable int id) {
        log.info("Inside getUsrById method of UserController class.");
        return service.getUserById(id);
    }

    // == Get Users by Paging ==
    @GetMapping("/AllUsersByPage")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @Secured("ROLE_ADMIN")  //it is similar for authorization
    public Page<UserDto> getAllUsersByPage(Pageable pageable) {
        log.info("Inside getAllUsersByPage method of UserController class.");
        return service.getAllUsersByPage(pageable);
    }

    // == Get users by Paging and Sorting ==
//    @GetMapping("/AllUsersByPagingAndSorting")
//    public Page<UserDto> getAllUsersByPagingAndSorting(@RequestParam Optional<Integer> page,
//                                                       @RequestParam Optional<Integer> size,
//                                                       @RequestParam Optional<String> sortBy) {
//        log.info("Inside UserController method getAllUsersByPagingAndSorting");
//        return service.getAllUsersByPage(PageRequest.of(page.orElse(0),  size.orElse(1), Sort.Direction.ASC, sortBy.orElse("userId")));
//    }

    @DeleteMapping("/removeUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> removeUser(@PathVariable int id){
        log.info("Inside removeUser method of UserController class.");
        return service.removeUser(id);
    }

    // == Updating User ==
    @PutMapping("/updateUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public UserDto updateUser(@PathVariable int id, @RequestBody User user) {
        log.info("Inside updateUser method of UserController class.");
        return service.updateUser(id, user);
    }

    // == Create Address ==
    @PatchMapping("/addAddress/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public UserDto addAddressToUse(@PathVariable int id, @RequestBody Address address) {
        log.info("Inside addAddressToUser method of UserController class.");
        return service.addAddressToUser(id, address);
    }

    // == Update Credentials ==
    @PatchMapping("/updateCredentials")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String updateCredentials(
            @RequestParam("password") String password,
            @RequestParam("oldPassword") String oldPassword, Principal principal) {
        log.info("Inside addAddressToUser method of UserController class.");
        return service.updateCredentials(password, oldPassword, principal);
    }

    // == Export CSV File ==
    @GetMapping("/downloadCSV")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void exportCSV(HttpServletResponse response) throws Exception {
        log.info("Inside exportCSV method of UserController class.");
        // set file name and content type
        String filename = "User-List.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        // create a csv writer
        StatefulBeanToCsv<UserDto> writer =
                new StatefulBeanToCsvBuilder<UserDto>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false).build();

        // write all employees to csv file
        writer.write(service.findAll());
    }
}
