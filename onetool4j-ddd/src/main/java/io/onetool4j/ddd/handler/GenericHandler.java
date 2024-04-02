package io.onetool4j.ddd.handler;

import io.onetool4j.ddd.dto.StandardResponse;
import io.onetool4j.exception.BizException;
import io.onetool4j.exception.ErrorConstant;
import io.onetool4j.util.LazyLogger;

/**
 * 基础处理器
 *
 * @author admin
 */
public abstract class GenericHandler<REQ, REP> {

    private final HandlerConfig handlerConfig;
    private final LazyLogger log;


    /**
     * 构造函数
     */
    public GenericHandler() {
        this.handlerConfig = getHandlerConfig();
        this.log = LazyLogger.getLogger(org.slf4j.LoggerFactory.getLogger(this.getClass()), handlerConfig.getSerializableType());
    }

    /**
     * 可通过重写此方法来自定义配置getHandlerConfig()方法获取配置
     *
     * @return 配置
     */
    protected HandlerConfig getHandlerConfig() {
        return HandlerConfig.of();
    }

    /**
     * 处理
     *
     * @param request 输入
     * @return 输出
     */
    public StandardResponse<REP> handle(REQ request) {
        try {
            log.info("handler exec start request={}", request);

            StandardResponse<REP> response = StandardResponse.ofSuccess(doHandle(request));
            log.info("handler exec completed response={}", response);
            return response;
        } catch (BizException e) {
            if ("WARN".equalsIgnoreCase(handlerConfig.getErrorLogLevel())) {
                log.warn("handler exec biz error ", e);
            } else {
                log.error("handler exec biz error ", e);
            }
            return StandardResponse.ofFail(e.getCode(), e.getMessage());
        } catch (Throwable e) {
            if ("WARN".equalsIgnoreCase(handlerConfig.getErrorLogLevel())) {
                log.warn("handler exec unknown error ", e);
            } else {
                log.error("handler exec unknown error ", e);
            }
            return StandardResponse.ofFail(ErrorConstant.SYSTEM_ERROR);
        }
    }

    abstract REP doHandle(REQ request);
}
