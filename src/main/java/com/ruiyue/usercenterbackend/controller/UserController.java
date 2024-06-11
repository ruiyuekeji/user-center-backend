package com.ruiyue.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruiyue.usercenterbackend.common.BaseResponse;
import com.ruiyue.usercenterbackend.common.ResultUtils;
import com.ruiyue.usercenterbackend.exception.BizException;
import com.ruiyue.usercenterbackend.exception.ErrorCode;
import com.ruiyue.usercenterbackend.model.domain.User;
import com.ruiyue.usercenterbackend.model.request.UserLoginRequest;
import com.ruiyue.usercenterbackend.model.request.UserRegisterRequest;
import com.ruiyue.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.RouterFunctionDslKt;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableScheduledFuture;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            return  null;
        }
        Long register = userService.userRegister(userAccount, userPassword, checkPassword);

        return ResultUtils.success(register);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) {
            throw new BizException(ErrorCode.BAD_REQUEST_PARAM,"请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            throw new BizException(ErrorCode.BAD_REQUEST_PARAM,"用户名或密码为空");
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }


    /**
     * 用户注销
     * @param request   请求
     * @return 1 表示注销成功
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BizException(ErrorCode.BAD_REQUEST_PARAM,"请求参数为空");
        }
        userService.userLogout(request);
        return ResultUtils.success(1);
    }

    /**
     * 获取当前用户登录信息
     * @param request   请求
     * @return 用户
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User user = getSessionUser(request);
        // 未登录
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_LOGIN,"用户未登录系统");
        }
        // 避免session中的user信息不实时，因此再次查库（可以设计为即时更新缓存，减少查库次数）
        User dbUser = userService.getById(user.getId());
        User safetyUser = userService.getSafetyUser(dbUser);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 用户查询，用户列表执行
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {

        if (!isAdmin(request)) {
            throw new BizException(ErrorCode.NO_AUTHORITY,"非管理员无权访问用户数据");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<User> list = userService.list(queryWrapper);
        List<User> userList = list.stream().map(user -> {
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }

    @GetMapping("/deleteUser")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST_PARAM,"请求参数不能小于0");
        }
        if (!isAdmin(request)) {
            throw new BizException(ErrorCode.NO_AUTHORITY,"非管理员无权删除用户");
        }

        boolean removeFlag = userService.removeById(id);
        return ResultUtils.success(removeFlag);
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
