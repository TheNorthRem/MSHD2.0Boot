package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.utils.Result;

public interface UserService extends IService<User> {
    /**
     * 登录接口，查询数据库中是否有对应的用户电话号码和密码
     *
     * @param phone    电话号码
     * @param password 密码
     * @return 登录结果
     */
    Result login(String phone, String password);

    /**
     * 注册接口
     *
     * @param userName 用户姓名
     * @param phone    电话号码
     * @param password 密码
     * @return 注册结果
     */
    Result register(String userName, String phone, String password);

    /**
     * 注销接口
     *
     * @param phone    电话号码
     * @param password 密码
     * @return 注销结果
     */
    Result delete(String phone, String password);
}
