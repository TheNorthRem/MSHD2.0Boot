package com.bupt.mshd2_0boot.dto;

import lombok.Data;

/**
 * 脱敏过的用户信息
 */

@Data
public class UserDTO {
    private Integer id;

    private String username;

    private String icon;

    private String phone;

    private Integer privilege;
}
