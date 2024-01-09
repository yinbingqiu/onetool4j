package io.github.onetool4j.ddd.handler;

import io.github.onetool4j.ddd.dto.Query;

/**
 * 普通查询处理器
 *
 * @author admin
 */
public abstract class QueryHandler<REQ extends Query, REP> extends GenericHandler<REQ, REP> {

    @Override
    REP doHandle(REQ request) {
        return query(request);
    }

    protected abstract REP query(REQ query);
}
