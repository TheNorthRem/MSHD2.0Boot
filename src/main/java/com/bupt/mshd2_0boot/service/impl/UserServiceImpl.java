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
import com.bupt.mshd2_0boot.utils.ThreadLocal.UserHolder;
import com.bupt.mshd2_0boot.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Result login(String phone, String password, Integer privilege) {
        // 验证手机号格式
        if (Tools.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 查询对应的电话号码和权限 select ... where phone = $phone and privilege = $privilege;
        queryWrapper.eq("phone", phone)
                .eq("privilege", privilege);
        long count = this.count(queryWrapper);
        // 未在数据库查询到用户信息
        if (count == 0) {
            return Result.fail("用户不存在或权限错误!");
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
        // 查询到说明已经被注册
        if (count != 0) {
            return Result.fail("用户名已经被注册!(电话号码重复)");
        }
        //privilege为0.默认是普通用户
        User user = new User(null, userName, password, phone, "", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 0);
        // 注册账号
        this.save(user);
        return Result.ok();
    }

    @Override
    public Result delete(String phone, String password) {
        // 校验手机号
        if (Tools.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }

        // 查询数据库中是否有对应数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            return Result.fail("用户不存在!");
        }
        if (!user.getPassword().equals(password)) {
            return Result.fail("密码错误!");
        }

        // 删除用户信息
        boolean result = this.remove(queryWrapper);
        if (!result) {
            log.error("数据库删除数据失败!");
            return Result.fail("数据库删除失败，请联系管理员!");
        }
        return Result.ok();
    }

    @Override
    public Result logout(String phone, String token) {
        // 检验手机号和token
        if (Tools.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }
        if (StrUtil.isBlank(token)) {
            return Result.fail("token为空!");
        }

        // 检验是否有用户
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        if (userMap.isEmpty()) {
            return Result.fail("用户token不存在!");
        }

        // 获取用户信息
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        // 核验电话号码
        if (!userDTO.getPhone().equals(phone)) {
            return Result.fail("电话号码错误");
        }

        // 删除redis中的token
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        return Result.ok();
    }

    @Override
    @Transactional
    public Result edit(User user, String token) {
        if (user == null) {
            return Result.fail("用户信息不能为空");
        }

        // 校验手机号,创建时间和token码
        if (user.getPhone() != null || user.getCreateTime() != null) {
            return Result.fail("不能修改手机号和创建时间!");
        }

        if (StrUtil.isBlank(token)) {
            return Result.fail("token为空!");
        }
        // 更新修改时间
        user.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        // 更新redis
        // User脱敏转换为Map
        Map<String, Object> userMap = BeanUtil.beanToMap(BeanUtil.copyProperties(user, UserDTO.class), new HashMap<>(), CopyOptions.create()
                .setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue == null ? null : fieldValue.toString())); //这里面的key和value都要是String结构来保证StringRedis这个东西能够顺利存储
        // 不修改手机号和id
        userMap.remove("phone");
        userMap.remove("id");

        // Hash存储 设置有效值
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 更新数据库
        this.updateById(user);
        return Result.ok();
    }

    @Override
    public Result message() {
        // 从ThreadLocal提取变量
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) { //null说明没有登录
            return Result.fail("用户未登录!");
        }
        return Result.ok(userDTO);
    }

    @Override
    public Result userList() {
        UserDTO userDTO = UserHolder.getUser();

        if (userDTO == null) {
            return Result.fail("管理员未登录!");
        }

        if (!userDTO.getPrivilege().equals(1)) {
            return Result.fail("权限不足!");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("privilege", 0);
        return Result.ok(this.list(queryWrapper));
    }
}
