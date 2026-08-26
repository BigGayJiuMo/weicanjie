package com.jiumo.weicanjie.exception;

import com.jiumo.weicanjie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器：统一捕获 Controller 层抛出的异常，转换为 Result 返回，
 * 保证前端拿到的永远是 { code, message, data, timestamp } 结构。
 * <p>
 * 优先级：@ExceptionHandler 按异常类型精确匹配，子类异常优先于父类。
 * 面试点：为什么能捕获 Controller 的异常？—— Spring MVC 的 DispatcherServlet 处理流程中，
 * HandlerExceptionResolver 负责解析处理器抛出的异常，@RestControllerAdvice 是它的实现之一。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：业务代码主动抛出，返回对应的业务错误码
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * @RequestBody 参数校验失败（DTO 上加了 @Valid 时触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: {}", msg);
        return Result.error(400, msg);
    }

    /**
     * 表单绑定参数校验失败（@ModelAttribute 时触发）
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数绑定失败: {}", msg);
        return Result.error(400, msg);
    }

    /**
     * 方法参数校验失败（@RequestParam / @PathVariable 上的校验注解时触发）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("约束校验失败: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 兜底异常：未知异常统一返回 500，避免堆栈信息直接暴露给前端
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "服务器内部错误");
    }
}
