package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.utils.Result;

public interface UserService extends IService<User> {
    /**
     * 登录接口
     *
     * @param phone    电话号码
     * @param password 密码
     * @param privilege 权限(1:管理员,0:普通用户)
     * @return 登录结果
     */
    Result login(String phone, String password, Integer privilege);

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

    /**
     * 登出接口
     *
     * @param phone 电话号码
     * @param token token码
     * @return 登出结果
     */
    Result logout(String phone, String token);

    /**
     * 编辑用户信息
     *
     * @param user  修改后的用户信息
     * @param token token码
     * @return 修改结果
     */
    Result edit(User user, String token);
}
