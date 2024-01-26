package io.github.onetool4j.exception;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 2024/1/7 15:50
 * 业务异常类
 *
 * @author admin
 */
public class BizException extends SummaryException {
    /**
     * 错误码
     */
    private String code;

    /**
     * 构造器
     *
     * @param errorCode  异常码
     * @param errMessage 异常信息
     * @param e          异常
     */
    private BizException(String errorCode, String errMessage, Throwable e) {
        super(errMessage, e, new HashSet<>(Arrays.asList(BizException.class.getName())));
        this.code = errorCode;
    }

    /**
     * 构造器
     *
     * @param errorCode 异常码
     * @param e         异常
     */
    private BizException(ErrorCode errorCode, Throwable e) {
        this(errorCode.getCode(), errorCode.getMessage(), e);
    }

    /**
     * 构造器
     *
     * @param errorCode 异常码
     * @param e         异常
     */
    public static BizException of(String errorCode, String errMessage, Throwable e) {
        return new BizException(errorCode, errMessage, e);
    }

    /**
     * 构造方法
     *
     * @param errorCode  异常码
     * @param errMessage 异常信息
     * @return BizException
     */
    public static BizException of(String errorCode, String errMessage) {
        return new BizException(errorCode, errMessage, null);
    }

    /**
     * 构造方法
     *
     * @param errMessage 异常信息
     * @return BizException
     */
    public static BizException of(String errMessage) {
        return new BizException(ErrorCode.ofFail(errMessage), null);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @param e         异常
     * @return BizException
     */
    public static BizException of(ErrorCode errorCode, Throwable e) {
        return new BizException(errorCode, e);
    }

    /**
     * 构造方法
     *
     * @param errorCode 异常码
     * @return BizException
     */
    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode, null);
    }

    /**
     * summaryCountThreshold
     *
     * @param countThreshold countThreshold
     * @return
     */
    public BizException summaryCountThreshold(int countThreshold) {
        setCountThreshold(countThreshold);
        return this;
    }

    /**
     * summaryCountThreshold
     *
     * @param durationThreshold durationThreshold
     * @return
     */
    public BizException summaryDurationThreshold(Duration durationThreshold) {
        setDurationThreshold((int) durationThreshold.toMillis());
        return this;
    }

    public BizException summaryDisable() {
        setSupportSummary(false);
        return this;
    }


    /**
     * message
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "[" + code + "]";
    }

    public String getCode() {
        return code;
    }
}
