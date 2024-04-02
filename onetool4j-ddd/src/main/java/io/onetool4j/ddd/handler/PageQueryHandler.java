package io.onetool4j.ddd.handler;

import io.onetool4j.ddd.dto.PageQuery;

/**
 * 分业务查询处理器
 *
 * @author admin
 */
public abstract class PageQueryHandler<REQ extends PageQuery, REP> extends GenericHandler<REQ, REP> {

    @Override
    REP doHandle(REQ request) {
        return query(request);
    }

    protected abstract REP query(REQ query);
}
