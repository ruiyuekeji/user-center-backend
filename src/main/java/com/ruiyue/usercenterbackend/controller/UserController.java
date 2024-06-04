package com.ruiyue.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruiyue.usercenterbackend.model.domain.User;
import com.ruiyue.usercenterbackend.model.request.UserLoginRequest;
import com.ruiyue.usercenterbackend.model.request.UserRegisterRequest;
import com.ruiyue.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruiyue.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.ruiyue.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
public class UserController {


    @Resource
    private UserService userService;

    @GetMapping("/test")
    public String test(){
        System.out.println("test");
        return null;
    }

    @PostMapping("/userRegister")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            return  null;
        }

        return userService.userRegister(userAccount,userPassword,checkPassword);
    }

    @PostMapping("/userLogin")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            return null;
        }

        return userService.userLogin(userAccount,userPassword,request);
    }

    @GetMapping("/searchUsers")
    public List<User> searchUsers(String username, HttpServletRequest request) {

        if (StringUtils.isBlank(username)) {
            return new ArrayList<>();
        }

        if (!isAdmin(request)) {
            return new ArrayList<>();
        }

        QueryWrapper <User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username",username);

        List<User> list = userService.list();
        return list.stream().map(user -> {
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
    }

    @GetMapping("/deleteUser")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (id <= 0) {
            return false;
        }
        if (!isAdmin(request)) {
            return false;
        }

        return userService.removeById(id);
    }

    /**
     * 是否管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }
}
