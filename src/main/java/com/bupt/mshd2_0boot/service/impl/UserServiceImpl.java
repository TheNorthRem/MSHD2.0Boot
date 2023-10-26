package com.bupt.mshd2_0boot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.mapper.UserMapper;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.service.UserService;
import com.bupt.mshd2_0boot.utils.Result;
import com.bupt.mshd2_0boot.utils.Utils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result login(String phone, String password) {
        if (Utils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        long count = this.count(queryWrapper);

        if (count == 0) {
            return Result.fail("用户不存在!");
        }

        User user = this.getOne(queryWrapper);
        if (StrUtil.isBlank(password) || !password.equals(user.getPassword())) {
            return Result.fail("密码错误!");
        }

        return Result.ok();
    }

    @Override
    public Result register(String userName, String phone, String password) {
        if (!Utils.checkData(userName, password, phone)) {
            return Result.fail("用户名/密码/手机号码不合要求！");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        long count = this.count(queryWrapper);

        if (count != 0) {
            return Result.fail("用户名已经被注册!");
        }

        User user = new User(null, userName, password, phone, "", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 0);
        this.save(user);
        return Result.ok();
    }
}
