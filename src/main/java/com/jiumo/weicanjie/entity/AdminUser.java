package com.jiumo.weicanjie.entity;

import lombok.Data;

@Data
public class AdminUser {

    private Long id;
    private String username;
    private String password;
    private String role;
    private Long restaurantId;
    private String createdTime;
}
