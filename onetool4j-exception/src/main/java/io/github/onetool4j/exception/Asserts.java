package io.github.onetool4j.exception;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 2024/1/7 15:51
 * 通用断言工具类
 *
 * @author admin
 */
public final class Asserts {
    /**
     * 正则表达式缓存
     */
    private static final Map<String, Pattern> patterns = new HashMap<>();

    /**
     * 构造方法隐藏
     */
    private Asserts() {
    }

    /**
     * 断言表达式为真
     *
     * @param expression 表达式
     * @param message    异常信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw BizException.of(ErrorConstant.PARAMETER_ERROR.code, message);
        }
    }

    /**
     * 断言表达式为真
     *
     * @param expression 表达式
     * @param errorCode  异常码
     */
    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw BizException.of(errorCode);
        }
    }

    /**
     * 断言表达式为真
     *
     * @param expression      表达式
     * @param messageSupplier 异常信息提供者
     */
    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw BizException.of(ErrorConstant.PARAMETER_ERROR.code, messageSupplier.get());
        }
    }

    /**
     * 断言表达式为假
     *
     * @param expression 表达式
     * @param message    异常信息
     */
    public static void notTrue(boolean expression, String message) {
        isTrue(!expression, message);
    }

    /**
     * 断言表达式为假
     *
     * @param expression 表达式
     * @param errorCode  异常码
     */
    public static void notTrue(boolean expression, ErrorCode errorCode) {
        isTrue(!expression, errorCode);
    }

    /**
     * 断言表达式为假
     *
     * @param expression      表达式
     * @param messageSupplier 异常信息提供者
     */
    public static void notTrue(boolean expression, Supplier<String> messageSupplier) {
        isTrue(!expression, messageSupplier);
    }

    /**
     * 断言表达式为null
     *
     * @param obj     对象
     * @param message 异常信息
     */
    public static void isNull(Object obj, String message) {
        isTrue(obj == null, message);
    }

    /**
     * 断言表达式为null
     *
     * @param obj       对象
     * @param errorCode 异常码
     */
    public static void isNull(Object obj, ErrorCode errorCode) {
        isTrue(obj == null, errorCode);
    }

    /**
     * 断言表达式为null
     *
     * @param obj             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void isNull(Object obj, Supplier<String> messageSupplier) {
        isTrue(obj == null, messageSupplier);
    }

    /**
     * 断言表达式不为null
     *
     * @param obj     对象
     * @param message 异常信息
     */
    public static void notNull(Object obj, String message) {
        isTrue(obj != null, message);
    }

    /**
     * 断言表达式不为null
     *
     * @param obj       对象
     * @param errorCode 异常码
     */
    public static void notNull(Object obj, ErrorCode errorCode) {
        isTrue(obj != null, errorCode);
    }

    /**
     * 断言表达式不为null
     *
     * @param obj             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void notNull(Object obj, Supplier<String> messageSupplier) {
        isTrue(obj != null, messageSupplier);
    }

    /**
     * 断言表达式为empty
     *
     * @param str     对象
     * @param message 异常信息
     */
    public static void isEmpty(String str, String message) {
        isTrue(str == null || str.trim().length() == 0, message);
    }

    /**
     * 断言表达式为empty
     *
     * @param str       对象
     * @param errorCode 异常码
     */
    public static void isEmpty(String str, ErrorCode errorCode) {
        isTrue(str == null || str.trim().length() == 0, errorCode);
    }

    /**
     * 断言表达式为empty
     *
     * @param str             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void isEmpty(String str, Supplier<String> messageSupplier) {
        isTrue(str == null || str.trim().length() == 0, messageSupplier);
    }

    /**
     * 断言表达式不为empty
     *
     * @param str     对象
     * @param message 异常信息
     */
    public static void notEmpty(String str, String message) {
        isTrue(str != null && str.trim().length() > 0, message);
    }

    /**
     * 断言表达式不为empty
     *
     * @param str       对象
     * @param errorCode 异常码
     */
    public static void notEmpty(String str, ErrorCode errorCode) {
        isTrue(str != null && str.trim().length() > 0, errorCode);
    }

    /**
     * 断言表达式不为empty
     *
     * @param str             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void notEmpty(String str, Supplier<String> messageSupplier) {
        isTrue(str != null && str.trim().length() > 0, messageSupplier);
    }

    /**
     * 断言表达式为空集合
     *
     * @param col     对象
     * @param message 异常信息
     */
    public static void isEmpty(Collection<?> col, String message) {
        isTrue(col == null || col.isEmpty(), message);
    }

    /**
     * 断言表达式为空集合
     *
     * @param col       对象
     * @param errorCode 异常码
     */
    public static void isEmpty(Collection<?> col, ErrorCode errorCode) {
        isTrue(col == null || col.isEmpty(), errorCode);
    }

    /**
     * 断言表达式为空集合
     *
     * @param col             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void isEmpty(Collection<?> col, Supplier<String> messageSupplier) {
        isTrue(col == null || col.isEmpty(), messageSupplier);
    }

    /**
     * 断言表达式不为空集合
     *
     * @param col     对象
     * @param message 异常信息
     */
    public static void notEmpty(Collection<?> col, String message) {
        isTrue(col != null && !col.isEmpty(), message);
    }

    /**
     * 断言表达式不为空集合
     *
     * @param col       对象
     * @param errorCode 异常码
     */
    public static void notEmpty(Collection<?> col, ErrorCode errorCode) {
        isTrue(col != null && !col.isEmpty(), errorCode);
    }

    /**
     * 断言表达式不为空集合
     *
     * @param col             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void notEmpty(Collection<?> col, Supplier<String> messageSupplier) {
        isTrue(col != null && !col.isEmpty(), messageSupplier);
    }

    /**
     * 断言表达式为空Map
     *
     * @param col     对象
     * @param message 异常信息
     */
    public static void isEmpty(Map<?, ?> col, String message) {
        isTrue(col == null || col.isEmpty(), message);
    }

    /**
     * 断言表达式为空Map
     *
     * @param col       对象
     * @param errorCode 异常码
     */
    public static void isEmpty(Map<?, ?> col, ErrorCode errorCode) {
        isTrue(col == null || col.isEmpty(), errorCode);
    }

    /**
     * 断言表达式为空Map
     *
     * @param col             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void isEmpty(Map<?, ?> col, Supplier<String> messageSupplier) {
        isTrue(col == null || col.isEmpty(), messageSupplier);
    }

    /**
     * 断言表达式不为空Map
     *
     * @param col     对象
     * @param message 异常信息
     */
    public static void notEmpty(Map<?, ?> col, String message) {
        isTrue(col != null && !col.isEmpty(), message);
    }

    /**
     * 断言表达式不为空Map
     *
     * @param col       对象
     * @param errorCode 异常码
     */
    public static void notEmpty(Map<?, ?> col, ErrorCode errorCode) {
        isTrue(col != null && !col.isEmpty(), errorCode);
    }

    /**
     * 断言表达式不为空Map
     *
     * @param col             对象
     * @param messageSupplier 异常信息提供者
     */
    public static void notEmpty(Map<?, ?> col, Supplier<String> messageSupplier) {
        isTrue(col != null && !col.isEmpty(), messageSupplier);
    }

    /**
     * 获取正则表达式
     *
     * @param regex 正则表达式
     * @return Pattern
     */
    private static Pattern getPattern(String regex) {
        return patterns.computeIfAbsent(regex, Pattern::compile);
    }

    /**
     * 断言表达式匹配正则表达式
     *
     * @param str     对象
     * @param pattern 正则表达式
     * @param message 异常信息
     */
    public static void regexMatch(String str, String pattern, String message) {
        isTrue(str != null && getPattern(pattern).matcher(str).matches(), message);
    }

    /**
     * 断言表达式匹配正则表达式
     *
     * @param str       对象
     * @param pattern   正则表达式
     * @param errorCode 异常码
     */
    public static void regexMatch(String str, String pattern, ErrorCode errorCode) {
        isTrue(str != null && getPattern(pattern).matcher(str).matches(), errorCode);
    }

    /**
     * 断言表达式匹配正则表达式
     *
     * @param str             对象
     * @param pattern         正则表达式
     * @param messageSupplier 异常信息提供者
     */
    public static void regexMatch(String str, String pattern, Supplier<String> messageSupplier) {
        isTrue(str != null && getPattern(pattern).matcher(str).matches(), messageSupplier);
    }

}
