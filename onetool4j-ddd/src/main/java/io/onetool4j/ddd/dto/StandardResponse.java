package io.onetool4j.ddd.dto;

import io.onetool4j.exception.ErrorCode;

/**
 * 标准返回对象
 *
 * @param <T> 返回数据类型
 * @author admin
 */
public class StandardResponse<T> extends DTO {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 错误码
     */
    private String errCode;
    /**
     * 错误信息
     */
    private String errMessage;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 构造方法隐藏
     */
    public StandardResponse() {
    }

    /**
     * 构造方法
     *
     * @param <T> 返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> ofSuccess() {
        return of(null, true, "200", "success");
    }

    /**
     * 构造方法
     *
     * @param data 返回数据
     * @param <T>  返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> ofSuccess(T data) {
        return of(data, true, "200", "success");
    }

    /**
     * 构造方法
     *
     * @param errCode    异常码
     * @param errMessage 异常信息
     * @param <T>        返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> ofFail(String errCode, String errMessage) {
        return of(null, false, errCode, errMessage);
    }

    /**
     * 构造方法
     *
     * @param data       返回数据
     * @param errCode    异常码
     * @param errMessage 异常信息
     * @param <T>        返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> ofFail(T data, String errCode, String errMessage) {
        return of(data, false, errCode, errMessage);
    }

    /**
     * 构造方法
     *
     * @param errCode 异常码
     * @param <T>     返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> ofFail(ErrorCode errCode) {
        return of(null, false, errCode.code, errCode.message);
    }

    /**
     * 构造方法
     *
     * @param data    返回数据
     * @param errCode 异常码
     * @param <T>     返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> ofFail(T data, ErrorCode errCode) {
        return of(data, false, errCode.code, errCode.message);
    }

    /**
     * 构造方法
     *
     * @param data    返回数据
     * @param success 是否成功
     * @param code    错误码
     * @param message 错误信息
     * @param <T>     返回数据类型
     * @return StandardResponse
     */
    public static <T> StandardResponse<T> of(T data, boolean success, String code, String message) {
        StandardResponse<T> response = new StandardResponse<>();
        response.data = data;
        response.success = success;
        response.errCode = code;
        response.errMessage = message;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
