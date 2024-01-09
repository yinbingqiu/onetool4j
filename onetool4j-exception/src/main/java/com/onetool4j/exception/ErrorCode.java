package com.onetool4j.exception;


import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author admin
 * @description 异常码
 * @date 2024/1/7 15:52
 */
public final class ErrorCode {

    private static final Pattern CODE_REGEX = Pattern.compile("^[A-Z0-9]{8}$");
    // 通用成功状态
    private static final String SUCCESS = "00000200";
    // 通用异常状态
    private static final String SYSTEM_ERROR = "00000500";
    /**
     * 通用异常,规范：
     * 1. 总长度：8位，0-3位：系统标识，3-5位：模块标识，5-8位：异常标识
     * 2. 允许出现数字以及大写字母，其他字符不允许出现
     */
    public String code;
    // 描述
    public String message;

    /**
     * 构造方法隐藏，全部通过of(...) 、copy 系列方法生成ErrorCode对象
     *
     * @param code
     * @param message
     */
    ErrorCode(String code, String message) {
        assert message != null && !Objects.equals("", message.trim());
        assert CODE_REGEX.matcher(code).matches();

        this.code = code;
        this.message = message;
    }

    public static ErrorCode of(String code, String message) {
        return new ErrorCode(code, message);
    }

    public static ErrorCode ofFail(String message) {
        return new ErrorCode(SYSTEM_ERROR, message);
    }

    public static ErrorCode ofFail() {
        return new ErrorCode(SYSTEM_ERROR, "未知系统异常");
    }

    public static ErrorCode ofSuccess() {
        return new ErrorCode(SUCCESS, "success");
    }


    public ErrorCode copyCode(String message) {
        return new ErrorCode(this.code, message);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
