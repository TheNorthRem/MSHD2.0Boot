package com.bupt.mshd2_0boot.controller;


import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.service.UserService;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/User")
@Slf4j
@Tag(name = "用户")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/login")
    @Operation(summary = "登录")
    @Parameters({@Parameter(name = "phone", description = "电话号码"),
            @Parameter(name = "password", description = "密码"),
            @Parameter(name = "privilege", description = "int类型,1管理员,0普通用户")})
    public Result login(@RequestParam(name = "phone") String phone, @RequestParam(name = "password") String password, @RequestParam(name = "privilege") Integer privilege) {
        return userService.login(phone, password, privilege);
    }

    @PostMapping("/register")
    @Operation(summary = "注册(注册后类型为普通用户)")
    @Parameters({@Parameter(name = "username", description = "用户名"),
            @Parameter(name = "password", description = "密码"),
            @Parameter(name = "phone", description = "手机号")
    })
    public Result register(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, @RequestParam(name = "phone") String phone) {
        return userService.register(username, phone, password);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "注销账号")
    @Parameters({@Parameter(name = "phone", description = "电话号码"),
            @Parameter(name = "password", description = "密码")})
    public Result delete(@RequestParam(name = "phone") String phone, @RequestParam(name = "password") String password) {
        return userService.delete(phone, password);
    }

    @GetMapping("/logout")
    @Operation(summary = "登出")
    @Parameters({@Parameter(name = "phone", description = "电话号码"),
            @Parameter(name = "token", description = "token码")})
    public Result logout(@RequestParam(name = "phone") String phone, @RequestParam(name = "token") String token) {
        return userService.logout(phone, token);
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑用户信息")
    @Parameters({@Parameter(name = "user", description = "编辑后的用户信息(手机号和createTime必须为null!)"),
            @Parameter(name = "token", description = "token码")})
    public Result edit(@RequestBody User user, @RequestParam(name = "token") String token) {
        return userService.edit(user, token);
    }

    @GetMapping("/message")
    @Operation(summary = "获取用户信息(未登录无法获取)", security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "authorization", in = ParameterIn.HEADER, description = "请求头")
    public Result message() {
        return userService.message();
    }
}
