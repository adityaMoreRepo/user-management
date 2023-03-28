package com.example.usermanagement.bootStrap;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CLIRunner implements CommandLineRunner {
    @Autowired
    private final UserRepository repo;

    public CLIRunner(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        //Here you can initialize the DB
//        Optional<User> opt = repo.findById(1);
//        opt.ifPresent(user -> System.out.println(user.getFirstName()));
//        repo.findAll()
//                .forEach(user -> System.out.println(user));

    }
}
