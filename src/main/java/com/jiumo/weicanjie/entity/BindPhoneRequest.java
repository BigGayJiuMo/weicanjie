package com.jiumo.weicanjie.entity;

import lombok.Data;

@Data
public class BindPhoneRequest {
    private Long userId;
    private String phone;
}