package com.bupt.mshd2_0boot.controller;


import com.bupt.mshd2_0boot.service.UserService;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/Login")
    @Operation(summary = "登录")
    @Parameters({@Parameter(name = "phone", description = "电话号码"), @Parameter(name = "password", description = "密码")})
    public Result Login(@RequestParam(name = "phone") String phone, @RequestParam(name = "password") String password) {
        return userService.login(phone, password);
    }

    @GetMapping("/register")
    @Operation(summary = "注册")
    @Parameters({@Parameter(name = "username", description = "用户名"), @Parameter(name = "password", description = "密码"),
            @Parameter(name = "phone", description = "手机号")
    })
    public Result Register(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, @RequestParam(name = "phone") String phone) {
        return userService.register(username, phone, password);
    }
}
