package io.onetool4j.ddd.handler;

import io.onetool4j.ddd.dto.PageDTO;
import io.onetool4j.ddd.dto.PageQuery;

/**
 * 分业务查询处理器
 *
 * @author admin
 */
public abstract class PageQueryHandler<REQ extends PageQuery, REP> extends GenericHandler<REQ, PageDTO<REP>> {

    @Override
    PageDTO<REP> doHandle(REQ request) {
        return query(request);
    }

    protected abstract PageDTO<REP> query(REQ query);
}
