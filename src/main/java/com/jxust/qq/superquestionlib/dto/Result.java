package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

@Data
public class Result {

    public Result() {

    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    private int code;
    private String message;
    private Object data;

    public static Result SUCCESS(Object data) {
        return new Result(200 ,"成功", data);
    }

    public static Result SUCCESS() {
        return new Result(200 ,"成功", null);
    }

    public static Result SUCCESS(String message, Object data) {
        return new Result(200, message, data);
    }

    public static Result FAILD(Object data){
        return new Result(201, "失败", data);
    }

    public static Result HASAUTH(Object data) {
        return new Result(202, "已授权,请勿重复请求", data);
    }

    public static Result SERVERERROR() {
        return new Result(500, "服务器内部错误,请稍后重试", null);
    }

    public static Result PERMISSIONERROR() {
        return new Result(403, "访问权限不足", null);
    }

    public static Result URINOTFOUND() {
        return new Result(404, "访问资源不存在,请检查URL参数", null);
    }

    public static Result PARAMS_MISSING(){
        return new Result(405, "请求参数不完整", null);
    }

    public static Result PARAMS_ERROR(){
        return new Result(410, "请求参数错误", null);
    }

    public static Result PARAMS_ERROR(Object data){
        return new Result(410, "请求参数错误", data);
    }
}
