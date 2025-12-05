package com.jiumo.weicanjie.dto;

import lombok.Data;

@Data
public class AdminLoginDTO {
    private String username;
    private String password;

    public AdminLoginDTO() {}
}
