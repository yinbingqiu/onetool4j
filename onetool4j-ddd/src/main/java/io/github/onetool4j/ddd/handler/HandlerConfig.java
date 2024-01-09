package io.github.onetool4j.ddd.handler;


import io.github.onetool4j.util.LazyLogger;

/**
 * 2024/1/25 13:38
 * 配置类
 *
 * @author yinbingqiu
 */

public class HandlerConfig {
    /**
     * 异常日志级别
     * IGNORE/INFO/WARN/ERROR/
     */
    private String errorLogLevel = "WARN";
    private LazyLogger.SerializableType serializableType = LazyLogger.SerializableType.FASTJSON;

    private  HandlerConfig() {
    }


    public static HandlerConfig of() {
        return new HandlerConfig();
    }

    public HandlerConfig errorLogLevel(String errorLogLevel) {
        this.errorLogLevel = errorLogLevel;
        return this;
    }

    public String getErrorLogLevel() {
        return errorLogLevel;
    }

    public LazyLogger.SerializableType getSerializableType() {
        return serializableType;
    }
}
