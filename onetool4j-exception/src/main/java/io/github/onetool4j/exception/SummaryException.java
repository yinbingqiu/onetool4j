package io.github.onetool4j.exception;

import io.github.onetool4j.util.SummaryExceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Set;

/**
 * 2024/1/7 15:50
 * 支持摘要的异常类
 *
 * @author admin
 */
public abstract class SummaryException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 异常合并次数阈值
     */
    private int countThreshold;
    /**
     * 异常合并时间阈值
     */
    private int durationThreshold;

    private boolean supportSummary;

    private Set<String> excludeStackTraces;

    /**
     * 构造方法
     *
     * @param errMessage        错误信息
     * @param e                 异常
     * @param supportSummary    是否合并异常
     * @param countThreshold    异常合并次数阈值
     * @param durationThreshold 异常合并时间阈值
     */
    SummaryException(String errMessage
            , Throwable e
            , boolean supportSummary
            , int countThreshold
            , Duration durationThreshold
            , Set<String> excludeStackTraces) {
        super(errMessage, e);
        this.supportSummary = supportSummary;
        this.countThreshold = countThreshold;
        this.durationThreshold = (int) durationThreshold.toMillis();
        this.excludeStackTraces = excludeStackTraces;
    }

    /**
     * 构造方法
     *
     * @param errMessage 错误信息
     * @param e          异常
     */
    SummaryException(String errMessage, Throwable e, Set<String> excludeStackTraces) {
        this(errMessage
                , e
                , true
                , Math.min(Integer.parseInt(System.getProperty("exception.supportSummary.count.threshold", "1000")), 10000)
                , Duration.ofMillis(Math.min(Integer.parseInt(System.getProperty("exception.supportSummary.seconds.threshold", "60")) * 1000, (int) Duration.ofDays(1).toMillis()))
                , excludeStackTraces);
    }

    SummaryException(String errMessage, Set<String> excludeStackTraces) {
        this(errMessage, null, excludeStackTraces);
    }

    /**
     * countThreshold
     *
     * @param countThreshold countThreshold
     */
    public void setCountThreshold(int countThreshold) {
        this.countThreshold = countThreshold;
    }

    /**
     * 设置异常合并时间阈值
     *
     * @param durationThreshold durationThreshold
     */
    public void setDurationThreshold(int durationThreshold) {
        this.durationThreshold = durationThreshold;
    }

    /**
     * supportSummary
     *
     * @param supportSummary supportSummary
     */
    public void setSupportSummary(boolean supportSummary) {
        this.supportSummary = supportSummary;
    }

    /**
     * 打印异常堆栈
     */
    @Override
    public void printStackTrace() {
        if (!supportSummary) {
            super.printStackTrace();
        } else {
            printStackTrace(System.out);
        }
    }

    /**
     * 打印异常堆栈
     *
     * @param s PrintStreamOrWriter
     */
    @Override
    public void printStackTrace(PrintStream s) {
        if (!supportSummary) {
            super.printStackTrace(s);
        } else {
            s.println(SummaryExceptions.getFullStackTrace(this,
                    s,
                    excludeStackTraces,
                    countThreshold,
                    durationThreshold));
        }
    }

    /**
     * 打印异常堆栈
     *
     * @param s PrintWriter
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        if (!supportSummary) {
            super.printStackTrace(s);
        } else {
            s.println(SummaryExceptions.getFullStackTrace(this,
                    s,
                    excludeStackTraces,
                    countThreshold,
                    durationThreshold));
        }
    }
}
