package com.alphasolutions.eventapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alphasolutions.eventapi.model.repository.UserDAO;

@RestController
public class Test {

    private final UserDAO userDAO;

    public Test(UserDAO userDAO) {
       this.userDAO = userDAO;
    }
    @GetMapping("/test")
    public String testApi() {
        return userDAO.findAll().toString();
    }
}
