package com.heima.model.common.enums;

public enum AppHttpCodeEnum {

    // 成功段固定为200
    SUCCESS(200,"操作成功"),
    // 登录段1~50
    NEED_LOGIN(1,"需要登录后操作"),
    LOGIN_PASSWORD_ERROR(2,"密码错误"),
    // TOKEN50~100
    TOKEN_INVALID(50,"无效的TOKEN"),
    TOKEN_EXPIRE(51,"TOKEN已过期"),
    TOKEN_REQUIRE(52,"TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100,"无效的SIGN"),
    SIG_TIMEOUT(101,"SIGN已过期"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"缺少参数"),
    PARAM_INVALID(501,"无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502,"图片格式有误"),
    UPLOAD_FILE_ERROR(502, "上传文件失败"),
    SERVER_ERROR(503,"服务器内部错误"),
    // 数据错误 1000~2000
    DATA_EXIST(1000,"数据已经存在"),
    AP_USER_DATA_NOT_EXIST(1001,"ApUser数据不存在"),
    DATA_NOT_EXIST(1002,"数据不存在"),
    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000,"无权限操作"),
    NEED_ADMIND(3001,"需要管理员权限"),
    NOT_PUBLISHED_THEN_NOT_DOWN_OR_UP(501,"当前文章并未发布，不能上下架"),
    NOT_EXIST_NEWS(1002,"文章不存在"),
    FAIL_PUBLISH_NEWS(201,"文章发布出错，请重试"),
    NOT_EXIST_USER(1002,"用户不存！"),
    CHANNEL_IS_ON(1002, "频道已启用，无法编辑或删除"),
    CHANNEL_IS_REFERNCE(1002, "频道已被引用，无法编辑");

    int code;
    String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
