package io.onetool4j.ddd.handler;

/**
 * 普通查询处理器
 *
 * @author admin
 */
public abstract class QueryHandler<REQ, REP> extends GenericHandler<REQ, REP> {

    @Override
    REP doHandle(REQ request) {
        return query(request);
    }

    protected abstract REP query(REQ query);
}
