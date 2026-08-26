package com.jiumo.weicanjie.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // 基础的成功和错误方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 常用的成功状态方法
    public static <T> Result<T> ok() {
        return success("操作成功");
    }

    public static <T> Result<T> ok(T data) {
        return success(data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return success(message, data);
    }

    // 常用的错误状态方法
    public static <T> Result<T> fail() {
        return error("操作失败");
    }

    public static <T> Result<T> fail(String message) {
        return error(message);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return error(code, message);
    }

    // 业务相关的状态方法
    public static <T> Result<T> notFound() {
        return error(404, "资源不存在");
    }

    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    public static <T> Result<T> unauthorized() {
        return error(401, "未授权");
    }

    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    public static <T> Result<T> forbidden() {
        return error(403, "禁止访问");
    }

    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }

    public static <T> Result<T> badRequest() {
        return error(400, "请求参数错误");
    }

    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    public static <T> Result<T> serverError() {
        return error(500, "服务器内部错误");
    }

    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }

    public static <T> Result<T> serviceUnavailable() {
        return error(503, "服务暂不可用");
    }

    public static <T> Result<T> serviceUnavailable(String message) {
        return error(503, message);
    }

    // 业务逻辑相关的方法
    public static <T> Result<T> dataExists() {
        return error(409, "数据已存在");
    }

    public static <T> Result<T> dataExists(String message) {
        return error(409, message);
    }

    public static <T> Result<T> dataNotExists() {
        return error(404, "数据不存在");
    }

    public static <T> Result<T> dataNotExists(String message) {
        return error(404, message);
    }

    public static <T> Result<T> operationNotAllowed() {
        return error(405, "操作不允许");
    }

    public static <T> Result<T> operationNotAllowed(String message) {
        return error(405, message);
    }

    // 验证相关的方法
    public static <T> Result<T> validationError() {
        return error(422, "参数验证失败");
    }

    public static <T> Result<T> validationError(String message) {
        return error(422, message);
    }

    // 分页查询成功方法
    public static <T> Result<PageResult<T>> pageSuccess(PageResult<T> pageData) {
        Result<PageResult<T>> result = new Result<>();
        result.setCode(200);
        result.setMessage("成功");
        result.setData(pageData);
        return result;
    }

    public static <T> Result<PageResult<T>> pageSuccess(String message, PageResult<T> pageData) {
        Result<PageResult<T>> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(pageData);
        return result;
    }

    // 判断结果是否成功
    // 注意：getter 若参与 JSON 序列化会多出 error/success 字段，导致缓存反序列化失败。
    // 压测时遇到过：Result 被缓存成 JSON 后读回时因未知字段 error 抛 UnrecognizedPropertyException，
    // 因此这些"逻辑判断"方法必须用 @JsonIgnore 排除。
    @JsonIgnore
    public boolean isSuccess() {
        return code != null && code == 200;
    }

    @JsonIgnore
    public boolean isError() {
        return code == null || code != 200;
    }

    // 链式调用方法
    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    // 获取数据的便捷方法
    public T getDataOrElse(T defaultValue) {
        return data != null ? data : defaultValue;
    }
}

// 分页结果封装类
@Data
class PageResult<T> {
    private Long total;
    private Integer page;
    private Integer size;
    private java.util.List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, Integer page, Integer size, java.util.List<T> list) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.list = list;
    }

    public static <T> PageResult<T> of(Long total, Integer page, Integer size, java.util.List<T> list) {
        return new PageResult<>(total, page, size, list);
    }

    public Integer getTotalPages() {
        if (size == null || size == 0) return 0;
        return (int) Math.ceil((double) total / size);
    }

    public Boolean hasNext() {
        if (page == null || size == null || total == null) return false;
        return (long) page * size < total;
    }

    public Boolean hasPrevious() {
        return page != null && page > 1;
    }
}