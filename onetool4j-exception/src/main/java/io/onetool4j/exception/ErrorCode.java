package io.onetool4j.exception;


import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 2024/1/7 15:52
 * 异常码
 *
 * @author admin
 */
public final class ErrorCode {
    /**
     * 异常码规范：正则表达式
     */
    private static final Pattern CODE_REGEX = Pattern.compile("^[A-Z0-9]{8}$");
    /**
     * 通用成功状态
     */
    private static final String SUCCESS = "00000200";
    /**
     * 通用异常状态
     */
    private static final String SYSTEM_ERROR = "00000500";
    /**
     * 通用异常,规范：
     * 1. 总长度：8位，0-3位：系统标识，3-5位：模块标识，5-8位：异常标识
     * 2. 允许出现数字以及大写字母，其他字符不允许出现
     */
    public String code;
    /**
     * 描述
     */
    public String message;

    /**
     * 构造方法隐藏，全部通过of(...) 、copy 系列方法生成ErrorCode对象
     *
     * @param code    异常码
     * @param message 描述
     */
    ErrorCode(String code, String message) {
        assert message != null && !Objects.equals("", message.trim());
        assert CODE_REGEX.matcher(code).matches();

        this.code = code;
        this.message = message;
    }

    /**
     * 生成ErrorCode对象
     *
     * @param code    异常码
     * @param message 描述
     * @return ErrorCode
     */
    public static ErrorCode of(String code, String message) {
        return new ErrorCode(code, message);
    }

    /**
     * 生成ErrorCode对象
     *
     * @param message 描述
     * @return ErrorCode
     */
    public static ErrorCode ofFail(String message) {
        return new ErrorCode(SYSTEM_ERROR, message);
    }

    /**
     * 生成ErrorCode对象
     *
     * @return ErrorCode
     */
    public static ErrorCode ofFail() {
        return new ErrorCode(SYSTEM_ERROR, "未知系统异常");
    }

    /**
     * 生成ErrorCode对象
     *
     * @return ErrorCode
     */
    public static ErrorCode ofSuccess() {
        return new ErrorCode(SUCCESS, "success");
    }

    /**
     * 生成ErrorCode对象
     *
     * @param message 描述
     * @return ErrorCode
     */
    public ErrorCode copyCode(String message) {
        return new ErrorCode(this.code, message);
    }

    /**
     * 生成ErrorCode对象
     *
     * @return ErrorCode
     */
    public String getCode() {
        return code;
    }

    /**
     * 生成ErrorCode对象
     *
     * @return ErrorCode
     */
    public String getMessage() {
        return message;
    }
}
