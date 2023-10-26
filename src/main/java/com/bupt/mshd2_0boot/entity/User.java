package com.bupt.mshd2_0boot.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Data
@TableName("user")
@Schema(description = "用户信息")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键，用户id")
    private Integer id;

    @TableField("username")
    @Schema(description = "用户名")
    private String username;

    @TableField("password")
    @Schema(description = "密码")
    private String password;

    @TableField("phone")
    @Schema(description = "电话号码")
    private String phone;

    @TableField("icon")
    @Schema(description = "头像")
    private String icon;

    @TableField("create_time")
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    @TableField("privilege")
    @Schema(description = "权限")
    private Integer privilege;
}
