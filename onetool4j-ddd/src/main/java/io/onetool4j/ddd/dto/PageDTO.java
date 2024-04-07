package io.onetool4j.ddd.dto;

import io.onetool4j.util.Reflections;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 标准分页对象
 *
 * @param <T> `data`的类型
 * @author admin
 */
public class PageDTO<T> {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;
    /**
     * 分页对象缓存
     */
    private final static Map<Class<?>, PageDTOClazzField> pagedto_clazz_cache = new ConcurrentHashMap<>();
    /**
     * 每页记录数
     */
    int size = 10;
    /**
     * 当前页码
     */
    int current = 1;
    /**
     * 总记录数
     */
    int total = 0;
    /**
     * 数据
     */
    List<T> records;

    /**
     * 构造方法隐藏
     */
    private PageDTO() {
    }

    /**
     * 空分页对象
     *
     * @param pageSize 每页记录数
     * @param <T>      数据类型
     * @return PageDTO
     */
    public static <T> PageDTO<T> ofEmpty(int pageSize) {
        return of(null, pageSize, 1, 0);
    }

    /**
     * 空分页对象
     *
     * @param pageSize    每页记录数
     * @param currentPage 当前页码
     * @param <T>         数据类型
     * @return PageDTO
     */
    public static <T> PageDTO<T> ofEmpty(int pageSize, int currentPage) {
        return of(null, pageSize, currentPage, 0);
    }

    /**
     * 空分页对象
     *
     * @param data        数据
     * @param pageSize    每页记录数
     * @param currentPage 当前页码
     * @param totalCnt    总记录数
     * @param <T>         数据类型
     * @return PageDTO
     */
    public static <T> PageDTO<T> of(List<T> data, int pageSize, int currentPage, int totalCnt) {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.size = pageSize;
        pageDTO.current = currentPage;
        pageDTO.records = data;
        pageDTO.total = totalCnt;
        return pageDTO;
    }

    /**
     * 空分页对象
     *
     * @param query 分页查询对象
     * @param <T>   数据类型
     * @return PageDTO
     */
    public static <T> PageDTO<T> ofEmpty(PageQuery query) {
        return of(null, query, 0);
    }

    /**
     * 空分页对象
     *
     * @param query    分页查询对象
     * @param totalCnt 总记录数
     * @param <T>      数据类型
     * @return PageDTO
     */
    public static <T> PageDTO<T> of(List<T> data, PageQuery query, int totalCnt) {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.size = query.getPageSize();
        pageDTO.current = query.getPageNum();
        pageDTO.records = data;
        pageDTO.total = totalCnt;
        return pageDTO;
    }

    /**
     * 复制分页对象复制并转换分页对象，
     * 复制：用于将开源的一些PageDTO对象复制为当前这个PageDTO工具，比如将mybatis-plus的Page对象复制为当前PageDTO工具
     *
     * @param originPageDTO 原始分页对象
     * @param <T>           原始分页对象的Data类型
     * @return 复制后的分页对象
     */
    public static <T> PageDTO<T> copy(Object originPageDTO) {
        PageDTOClazzField normalPageDTO = getNormalPageDTO(originPageDTO);
        if (normalPageDTO == null) {
            throw new IllegalArgumentException("不是标准分页对象XxxPageDTO(records,size,total,current)");
        }
        try {
            return of((List<T>) normalPageDTO.records.get(originPageDTO), Integer.parseInt(Objects.toString(normalPageDTO.pageSize.get(originPageDTO))), Integer.parseInt(Objects.toString(normalPageDTO.currentPage.get(originPageDTO))), Integer.parseInt(Objects.toString(normalPageDTO.totalCount.get(originPageDTO))));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制并转换分页对象，
     * 复制：用于将开源的一些PageDTO对象复制为当前这个PageDTO工具，比如将mybatis-plus的Page对象复制为当前PageDTO
     * 转化：将Data的类型转化为另外一种类型，比如将UserDTO转化为UserVO
     *
     * @param originPageDTO 原始分页对象
     * @param convert       转换函数
     * @param <T>           原始分页对象的Data类型
     * @param <R>           转换后的分页对象的Data类型
     * @return 复制并转换后的分页对象
     */
    public static <T, R> PageDTO<R> copyAndConvert(Object originPageDTO, Function<List<T>, List<R>> convert) {
        PageDTO<T> copyPageDTO = copy(originPageDTO);
        return of(convert.apply(copyPageDTO.getRecords()), copyPageDTO.getSize(), copyPageDTO.getCurrent(), copyPageDTO.getTotal());
    }

    /**
     * 复制并转换分页对象结构,有更多的分页对象类型可以在这个方法兼容
     *
     * @param originPageDTO 原始分页对象
     * @return 通用的分页对象结构cache
     */
    private static PageDTOClazzField getNormalPageDTO(Object originPageDTO) {
        return pagedto_clazz_cache.computeIfAbsent(originPageDTO.getClass(), key -> {
            PageDTOClazzField pageDTOClazzField = new PageDTOClazzField();
            pageDTOClazzField.records = Reflections.getField(key, "records");
            pageDTOClazzField.pageSize = Reflections.getField(key, "size");
            pageDTOClazzField.totalCount = Reflections.getField(key, "total");
            pageDTOClazzField.currentPage = Reflections.getField(key, "current");

            assert pageDTOClazzField.isPageDTO();

            pageDTOClazzField.records.setAccessible(true);
            pageDTOClazzField.pageSize.setAccessible(true);
            pageDTOClazzField.totalCount.setAccessible(true);
            pageDTOClazzField.currentPage.setAccessible(true);

            return pageDTOClazzField;
        });
    }

    /**
     * page size
     *
     * @return int
     */
    public int getSize() {
        return size;
    }

    /**
     * current page
     *
     * @return int
     */
    public int getCurrent() {
        return current;
    }

    /**
     * next page
     *
     * @return int
     */
    public int getNextPage() {
        return current + 1;
    }

    /**
     * previous page
     *
     * @return int
     */
    public int getPreviousPage() {
        return current <= 1 ? 1 : current - 1;
    }

    /**
     * has next page
     *
     * @return data
     */
    public List<T> getRecords() {
        return records;
    }

    /**
     * total count
     *
     * @return int
     */
    public int getTotal() {
        return total;
    }

    /**
     * total pages
     *
     * @return int
     */
    public int getTotalPages() {
        return total / size + 1;
    }

    /**
     * 分页对象结构缓存
     */
    static class PageDTOClazzField {
        /**
         * 数据
         */
        Field records;
        /**
         * 总记录数
         */
        Field totalCount;
        /**
         * 当前页码
         */
        Field currentPage;
        /**
         * 每页记录数
         */
        Field pageSize;
        /**
         * 构造方法隐藏
         */
        PageDTOClazzField() {
        }

        /**
         * 是否是标准分页对象
         *
         * @return boolean
         */
        boolean isPageDTO() {
            return records != null && totalCount != null && currentPage != null && pageSize != null;
        }

    }
}
