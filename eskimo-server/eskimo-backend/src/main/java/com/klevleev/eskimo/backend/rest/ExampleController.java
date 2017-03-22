package com.klevleev.eskimo.backend.rest;

import com.klevleev.eskimo.backend.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by stepank on 21.03.2017.
 */
@RestController
public class ExampleController {

    @Autowired
    private UserDao userDao;

    @RequestMapping("/api/users")
    public String get() {
        return userDao.getAllUsers().toString();
    }

}
