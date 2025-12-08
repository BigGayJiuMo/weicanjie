package com.jiumo.weicanjie.dto;

import lombok.Data;

/**
 * 用户绑定手机请求数据传输对象
 * 该类封装了用户绑定手机时需要的用户ID和手机号码。
 */
@Data
public class BindPhoneRequest {
    private Long userId;   // 用户ID
    private String phone;  // 绑定的手机号码
}
