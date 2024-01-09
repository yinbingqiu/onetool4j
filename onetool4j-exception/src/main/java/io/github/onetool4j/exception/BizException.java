package io.github.onetool4j.exception;

/**
 * 2024/1/7 15:50
 * 业务异常类
 *
 * @author admin
 */
public class BizException extends BaseException {
    /**
     * 构造器
     *
     * @param errorCode  异常码
     * @param errMessage 异常信息
     * @param e          异常
     * @param merge      是否合并异常信息
     */
    public BizException(String errorCode, String errMessage, Throwable e, boolean merge) {
        this(ErrorCode.of(errorCode, errMessage), e, merge);
    }

    /**
     * 构造器
     *
     * @param errorCode 异常码
     * @param e         异常
     * @param merge     是否合并异常信息
     */
    private BizException(ErrorCode errorCode, Throwable e, boolean merge) {
        super(errorCode, e, merge);
    }

    /**
     * 构造器
     *
     * @param errorCode 异常码
     * @param e         异常
     */
    public static BizException of(String errorCode, String errMessage, Throwable e) {
        return new BizException(errorCode, errMessage, e, false);
    }

    /**
     * 构造方法
     *
     * @param errorCode  异常码
     * @param errMessage 异常信息
     * @return BizException
     */
    public static BizException of(String errorCode, String errMessage) {
        return new BizException(errorCode, errMessage, null, false);
    }

    /**
     * 构造方法
     *
     * @param errMessage 异常信息
     * @return BizException
     */
    public static BizException of(String errMessage) {
        return new BizException(ErrorCode.ofFail(errMessage), null, false);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @param e         异常
     * @return BizException
     */
    public static BizException of(ErrorCode errorCode, Throwable e) {
        return new BizException(errorCode, e, false);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @return BizException
     */
    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode, null, false);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @param e         异常
     * @return BizException
     */
    public static BizException ofMerge(String errorCode, String errMessage, Throwable e) {
        return new BizException(errorCode, errMessage, e, true);
    }

    /**
     * 构造方法
     *
     * @param errorCode  异常码
     * @param errMessage 异常信息
     * @return BizException
     */
    public static BizException ofMerge(String errorCode, String errMessage) {
        return new BizException(errorCode, errMessage, null, true);
    }

    /**
     * 构造方法
     *
     * @param errMessage 异常信息
     * @return BizException
     */
    public static BizException ofMerge(String errMessage) {
        return new BizException(ErrorCode.ofFail(errMessage), null, true);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @param e         异常
     * @return BizException
     */
    public static BizException ofMerge(ErrorCode errorCode, Throwable e) {
        return new BizException(errorCode, e, true);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @return BizException
     */
    public static BizException ofMerge(ErrorCode errorCode) {
        return new BizException(errorCode, null, true);
    }
}
