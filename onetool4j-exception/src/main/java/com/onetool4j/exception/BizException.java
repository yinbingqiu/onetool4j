package com.onetool4j.exception;

/**
 * @author admin
 * @description 业务异常类
 * @date 2024/1/7 15:50
 */
public class BizException extends BaseException {
    public BizException(String errorCode, String errMessage, Throwable e, boolean merge) {
        this(ErrorCode.of(errorCode, errMessage), e, merge);
    }

    private BizException(ErrorCode errorCode, Throwable e, boolean merge) {
        super(errorCode, e, merge);
    }


    public static BizException of(String errorCode, String errMessage, Throwable e) {
        return new BizException(errorCode, errMessage, e, false);
    }

    public static BizException of(String errorCode, String errMessage) {
        return new BizException(errorCode, errMessage, null, false);
    }

    public static BizException of(String errMessage) {
        return new BizException(ErrorCode.ofFail(errMessage), null, false);
    }

    public static BizException of(ErrorCode errorCode, Throwable e) {
        return new BizException(errorCode, e, false);
    }

    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode, null, false);
    }

    public static BizException ofMerge(String errorCode, String errMessage, Throwable e) {
        return new BizException(errorCode, errMessage, e, true);
    }

    public static BizException ofMerge(String errorCode, String errMessage) {
        return new BizException(errorCode, errMessage, null, true);
    }

    public static BizException ofMerge(String errMessage) {
        return new BizException(ErrorCode.ofFail(errMessage), null, true);
    }

    public static BizException ofMerge(ErrorCode errorCode, Throwable e) {
        return new BizException(errorCode, e, true);
    }

    public static BizException ofMerge(ErrorCode errorCode) {
        return new BizException(errorCode, null, true);
    }
}
