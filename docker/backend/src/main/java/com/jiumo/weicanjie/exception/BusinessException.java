package com.jiumo.weicanjie.exception;

import lombok.Getter;

/**
 * 业务异常：业务逻辑校验不通过时抛出，由 GlobalExceptionHandler 统一捕获并转换为 Result 返回。
 * <p>
 * 用法示例：
 * <pre>
 * if (order == null) {
 *     throw new BusinessException(404, "订单不存在");
 * }
 * </pre>
 * 面试点：为什么不用返回 Result.error() 而是抛异常？
 * 答：业务代码只关心"业务流"，错误处理集中到全局异常处理器，避免每个方法都写 try-catch，
 * 也保证所有异常路径的返回结构一致（统一 Result 格式）。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
