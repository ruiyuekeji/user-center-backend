package com.ruiyue.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiyue.usercenterbackend.mapper.UserMapper;
import com.ruiyue.usercenterbackend.model.domain.User;
import com.ruiyue.usercenterbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.ruiyue.usercenterbackend.constant.UserConstant.SALT;
import static com.ruiyue.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author zhangruiyue
*  针对表【user】的数据库操作Service实现
*  2024-05-11 15:01:38
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {


        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            return -1; // 表示参数为空
        }
        if (userAccount.length() < 4) {
            return -2; // 用户账号过短
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -3; // 密码过短
        }

        // 账号中不能包含特殊字符
        String reg = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        if (userAccount.matches(reg)) {
            return -5; // 账号包含特殊字符
        }

        // 密码和校验密码必须相同
        if (!userPassword.equals(checkPassword)) {
            return -6; // 密码和校验密码不相同
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -4; // 账号重复
        }

        // 密码加密
        String digest = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 2. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUsername(userAccount);
        user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1759786464587145217/rDzTnV4L-微信图片_20240418162121.jpg");
        user.setUserPassword(digest);


        int result = userMapper.insert(user);
        return result;
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (userAccount.length() < 4) {
            return null; // 用户账号过短
        }

        if (userPassword.length() < 8) {
            return null; // 密码过短
        }

        // 账号中不能包含特殊字符
        String reg = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        if (userAccount.matches(reg)) {
            return null; // 账号包含特殊字符
        }



        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.info("User does not exist，userAccount:{}", userAccount);
            return null;
        }

        String userPasswordDb = user.getUserPassword();

        // 密码加密

        String digest = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());


        if (userPasswordDb.equals(digest)) {
            log.info("User login successful，userAccount:{}",userAccount);
            HttpSession session = request.getSession();

            // 用户脱敏
            User safetyUser = getSafetyUser(user);

            session.setAttribute(USER_LOGIN_STATE, safetyUser);

            return safetyUser;
        }


        return null;
    }

    public static void main(String[] args) {
        String s = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
        System.out.println(s);
    }

    /**
     * 用户信息脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user) {
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }
}




