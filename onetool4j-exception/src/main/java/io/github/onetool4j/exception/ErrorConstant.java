package io.github.onetool4j.exception;

/**
 * 2024/1/9 22:20
 * 异常码常量
 *
 * @author admin
 */
public interface ErrorConstant {
    /**
     * 通用成功状态
     */
    ErrorCode SUCCESS = ErrorCode.ofSuccess();
    /**
     * 通用异常状态
     */
    ErrorCode SYSTEM_ERROR = ErrorCode.ofFail();
    /**
     * 参数异常
     */
    ErrorCode PARAMETER_ERROR = ErrorCode.ofFail("参数异常");

}
