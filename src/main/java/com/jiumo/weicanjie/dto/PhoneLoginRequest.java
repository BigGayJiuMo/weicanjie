package com.jiumo.weicanjie.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 手机号登录请求 DTO
 * 仅用于手机号登录接口(/user/loginByPhone)
 * 注意:与微信登录(LoginRequest)区分——微信登录不要求手机号
 */
@Data
public class PhoneLoginRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;  // 手机号
}
