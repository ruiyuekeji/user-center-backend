package com.ruiyue.usercenterbackend.service;

import com.ruiyue.usercenterbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author zhangruiyue
* @description 针对表【user】的数据库操作Service
* @createDate 2024-05-11 15:01:38
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 用户校验密码
     * @return 注册成功-返回用户id，注册失败-抛出异常
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 登录成功-返回用户信息，登录失败-抛出异常
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User user);
}
