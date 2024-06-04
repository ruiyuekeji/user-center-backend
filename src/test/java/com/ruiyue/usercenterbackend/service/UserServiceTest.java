package com.ruiyue.usercenterbackend.service;
import java.util.Date;

import com.ruiyue.usercenterbackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import java.util.Collections;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void addUser() {
        User user = new User();
        user.setUsername("张三");
        user.setUserAccount("001");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);


        userService.save(user);
        System.out.println(user.getId());
        Assert.notEmpty(Collections.singleton(user.getId()), "用户id不能为空");
    }

    @Test
    void updateUser(){
        User user = new User();
        user.setId(1789217096645558273L);

        user.setUsername("张三123");
        userService.saveOrUpdate(user);

        User user1 = userService.getById(1789217096645558273L);
        System.out.println(user1.getUsername());
        Assert.notNull(user1, "用户不存在");

    }
}