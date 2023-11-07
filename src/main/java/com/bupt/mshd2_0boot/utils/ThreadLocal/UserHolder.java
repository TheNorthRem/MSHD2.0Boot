package com.bupt.mshd2_0boot.utils.ThreadLocal;

import com.bupt.mshd2_0boot.dto.UserDTO;

/**
 * ThreadLocal
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> userDTOThreadLocal = new ThreadLocal<>();

    public static void saveUser(UserDTO userDTO) {
        userDTOThreadLocal.set(userDTO);
    }

    public static UserDTO getUser() {
        return userDTOThreadLocal.get();
    }

    public static void removeUser() {
        userDTOThreadLocal.remove();
    }
}
