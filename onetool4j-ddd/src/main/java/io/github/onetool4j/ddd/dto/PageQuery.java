package io.github.onetool4j.ddd.dto;

/**
 * 标准分页参数
 *
 * @author admin
 */
public abstract class PageQuery extends Query {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 每页条数
     */
    private int pageSize = 10;
    /**
     * 当前页码
     */
    private int pageNum = 1;

    /**
     * page size
     *
     * @return int
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * page num
     *
     * @return int
     */
    public int getPageNum() {
        return pageNum;
    }
}
