package com.bupt.mshd2_0boot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.dto.UserDTO;
import com.bupt.mshd2_0boot.mapper.UserMapper;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.service.UserService;
import com.bupt.mshd2_0boot.utils.Result;
import com.bupt.mshd2_0boot.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import static com.bupt.mshd2_0boot.utils.RedisConstants.LOGIN_USER_KEY;
import static com.bupt.mshd2_0boot.utils.RedisConstants.LOGIN_USER_TTL;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result login(String phone, String password) {
        // 验证手机号格式
        if (Tools.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        long count = this.count(queryWrapper);
        // 未在数据库查询到用户信息
        if (count == 0) {
            return Result.fail("用户不存在!");
        }
        // 因为phone是唯一健，所以查询的时候只会有一条数据
        User user = this.getOne(queryWrapper);
        if (StrUtil.isBlank(password) || !password.equals(user.getPassword())) { //密码对不上
            return Result.fail("密码错误!");
        }

        // 保存用户信息到redis中
        // 生成随机token作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 用户信息脱敏
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // User转换为Map
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create()
                .setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString())); //这里面的key和value都要是String结构来保证StringRedis这个东西能够顺利存储
        // Hash存储 设置有效值
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 返回token
        return Result.ok(token);
    }

    @Override
    public Result register(String userName, String phone, String password) {
        // 检查电话号码，账号密码的格式
        if (!Tools.checkData(userName, password, phone)) {
            return Result.fail("用户名/密码/手机号码不合要求！");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        long count = this.count(queryWrapper);
        // 查询到说明应该被注册
        if (count != 0) {
            return Result.fail("用户名已经被注册!");
        }

        User user = new User(null, userName, password, phone, "", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 0);
        // 注册账号
        this.save(user);
        return Result.ok();
    }

    @Override
    public Result delete(String phone, String password) {
        if (Tools.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            return Result.fail("用户不存在!");
        }
        if (!user.getPassword().equals(password)) {
            return Result.fail("密码错误!");
        }
        boolean result = this.remove(queryWrapper);
        if (!result) {
            log.error("数据库删除数据失败!");
            return Result.fail("数据库删除失败，请联系管理员!");
        }
        return Result.ok();
    }
}
