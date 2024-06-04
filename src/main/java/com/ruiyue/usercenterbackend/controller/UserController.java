package com.ruiyue.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruiyue.usercenterbackend.model.domain.User;
import com.ruiyue.usercenterbackend.model.request.UserLoginRequest;
import com.ruiyue.usercenterbackend.model.request.UserRegisterRequest;
import com.ruiyue.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruiyue.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.ruiyue.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
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

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
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

    /**
     * 获取当前用户登录信息
     * @param request   请求
     * @return 用户
     */
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request){
        User user = getSessionUser(request);
        // 未登录
        if (user == null) {
            return null;
        }
        // 避免session中的user信息不实时，因此再次查库（可以设计为即时更新缓存，减少查库次数）
        User dbUser = userService.getById(user.getId());
        User safetyUser = userService.getSafetyUser(dbUser);
        return safetyUser;
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {

        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<User> list = userService.list(queryWrapper);
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

    /**
     * 从session中获取用户
     * @param request 请求
     * @return 返回user对象，可能返回null
     */
    private User getSessionUser(HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        return user;
    }
}
