package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.utils.Result;

public interface UserService extends IService<User> {
    Result login(String phone, String password);

    Result register(String userName, String phone, String password);
}
