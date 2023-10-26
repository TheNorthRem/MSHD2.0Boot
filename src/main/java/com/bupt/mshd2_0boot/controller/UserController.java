package com.bupt.mshd2_0boot.controller;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.service.UserService;
import com.bupt.mshd2_0boot.utils.Result;
import com.bupt.mshd2_0boot.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/User")
@Slf4j
@Tag(name="用户")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }


    @GetMapping("/Login")
    @Operation(summary = "登录")
    @Parameters({@Parameter(name = "username",description = "用户名"),@Parameter(name = "password",description = "密码")})
    public Result Login(@RequestParam(name = "username") String username, @RequestParam(name="password") String password){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("username",username);

        long count = userService.count(queryWrapper);

        if(count==0){
            return Result.fail("用户名不存在");
        }

        if (count > 1) { //判断用户名是否出现重复
            log.error("发现数据库出现重复数据！" + "userName:" + username);
            return Result.fail("用户名重复，请联系客服!");
        }

        User user = userService.getOne(queryWrapper);

        if(password.equals(user.getPassword())){
            JSONObject res =new JSONObject();
            res.put("userId",user.getId());

            return Result.ok(res);
        }

        return Result.fail("用户名或者密码错误");
    }

    @RequestMapping("/register")
    @Operation(summary = "注册")
    @Parameters({@Parameter(name = "username",description = "用户名"),@Parameter(name = "password",description = "密码"),
        @Parameter(name = "phone",description = "手机号")
    })
    public Result Register(@RequestBody JSONObject data){
        String username = data.getString("username");
        String password = data.getString("password");
        String phone = data.getString("phone");


        //检查密码是否合规
        if (!Utils.checkData(username, password,phone)) {
            return Result.fail("用户名或密码不合要求！");
        }


        //用户名注册判重
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userService.count(queryWrapper) != 0) {
            return Result.fail("用户名已被注册！");
        }

        User user = new User();

        user.setUsername(username);
        user.setPassword(password);
        //注册的时候默认nickname == username
        user.setNickName(username);
        user.setPrivilege(0);
        //将用户存入bs_user表中
        if (!userService.save(user)) {
            log.error("bs_user表插入失败!");
            return Result.fail("注册失败，请联系客服");
        }

        try {
            userService.list(queryWrapper).get(0).getId();
        } catch (IndexOutOfBoundsException exception) {
            log.error("user表出现错误!");
            return Result.fail("后台出现错误，请联系客服!");
        }

        return Result.ok("注册成功!");

    }

}
