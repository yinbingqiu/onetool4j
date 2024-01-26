package io.github.onetool4j.util;

import io.github.onetool4j.exception.Asserts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2024/1/25 13:56
 * 延迟执行Logger工具类
 *
 * @author yinbingqiu
 */
public class LazyLogger {

    public static final List<Class<?>> support_logger_types = new ArrayList<>();

    static {
        try {
            if (Reflections.hasClass("org.slf4j.Logger")) {
                support_logger_types.add(Class.forName("org.slf4j.Logger"));
            }

            if (Reflections.hasClass("org.apache.logging.log4j.Logger")) {
                support_logger_types.add(Class.forName("org.apache.logging.log4j.Logger"));
            }

            if (Reflections.hasClass("org.apache.log4j.Logger")) {
                support_logger_types.add(Class.forName("org.apache.log4j.Logger"));
            }
            if (Reflections.hasClass("ch.qos.logback.classic.Logger")) {
                support_logger_types.add(Class.forName("ch.qos.logback.classic.Logger"));
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Logger
     */
    private Object wrapped;
    /**
     * 实例化类型
     */
    private SerializableType serializableType;
    /**
     * fastjson序列化方法
     */
    private Method fastjsonMethod;
    /**
     * 日志方法
     */
    private Method traceMethod;
    /**
     * 日志方法
     */
    private Method isTraceEnabledMethod;
    /**
     * 日志方法
     */
    private Method debugMethod;
    /**
     * 日志方法
     */
    private Method isDebugEnabledMethod;
    /**
     * 日志方法
     */
    private Method infoMethod;
    /**
     * 日志方法
     */
    private Method isInfoEnabledMethod;
    /**
     * 日志方法
     */
    private Method warnMethod;
    /**
     * 日志方法
     */
    private Method isWarnEnabledMethod;
    /**
     * 日志方法
     */
    private Method errorMethod;
    /**
     * 日志方法
     */
    private Method isErrorEnabledMethod;

    private static final Pattern pattern = Pattern.compile("\\{ *\\}");
    private static final Set<String> excludeClassList = new HashSet<>(Arrays.asList(
            LazyLogger.class.getName()
            , Asserts.class.getName()));
    private int countThreshold = Math.min(Integer.parseInt(System.getProperty("exception.supportSummary.count.threshold", "10")), 10000);

    /**
     * 获取LazyLogger实例
     */
    private LazyLogger() {
    }

    /**
     * 获取LazyLogger实例
     */
    private LazyLogger(Object log, SerializableType serializableType) {
        assert log != null;
        if (SerializableType.FASTJSON.equals(serializableType)) {
            if (Reflections.hasClass("com.alibaba.fastjson.JSON")) {
                fastjsonMethod = Reflections.getMethod("com.alibaba.fastjson.JSON", "toJSONString", Object.class);
            } else if (Reflections.hasClass("com.alibaba.fastjson2.JSON")) {
                fastjsonMethod = Reflections.getMethod("com.alibaba.fastjson2.JSON", "toJSONString", Object.class);
            } else {
                throw new IllegalArgumentException("当前运行环境未引入fastjson依赖,无法支持fastjson的日志参数序列化");
            }
        }

        /**
         * 检查是否支持的日志类型
         */
        support_logger_types.stream().filter(loggerType -> loggerType.isInstance(log))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的日志类型:" + log.getClass().getName()));

        this.traceMethod = Reflections.getMethod(log.getClass(), "trace", String.class, Object[].class);
        this.isTraceEnabledMethod = Reflections.getMethod(log.getClass(), "isTraceEnabled");
        this.debugMethod = Reflections.getMethod(log.getClass(), "debug", String.class, Object[].class);
        this.isDebugEnabledMethod = Reflections.getMethod(log.getClass(), "isDebugEnabled");
        this.infoMethod = Reflections.getMethod(log.getClass(), "info", String.class, Object[].class);
        this.isInfoEnabledMethod = Reflections.getMethod(log.getClass(), "isInfoEnabled");
        this.warnMethod = Reflections.getMethod(log.getClass(), "warn", String.class, Object[].class);
        this.isWarnEnabledMethod = Reflections.getMethod(log.getClass(), "isWarnEnabled");
        this.errorMethod = Reflections.getMethod(log.getClass(), "error", String.class, Object[].class);
        this.isErrorEnabledMethod = Reflections.getMethod(log.getClass(), "isErrorEnabled");
        this.wrapped = log;
        this.serializableType = serializableType;
    }
    private int durationThreshold = Math.min(Integer.parseInt(System.getProperty("exception.supportSummary.seconds.threshold", "60")) * 1000, (int) Duration.ofDays(1).toMillis());
    private boolean supportSummary = true;

    /**
     * 延迟执行
     *
     * @param supplier supplier
     * @param <T>      返回值类型
     * @return LazyExecutor 实例
     */
    public static <T> LazyExecutor<T> lazyEval(Supplier<T> supplier) {
        assert supplier != null;
        return new LazyExecutor<>(supplier);
    }

    /**
     * @param log Logger 仅支持 support_logger_types 中的类型org.slf4j.Logger, org.apache.logging.log4j.Logger, org.apache.log4j.Logger, ch.qos.logback.classic.Logger
     * @return LazyLogger 实例
     */
    public static LazyLogger getLogger(Object log) {
        return getLogger(log, SerializableType.RAW);
    }

    /**
     * 获取实例
     *
     * @param log              Logger 仅支持 support_logger_types 中的类型org.slf4j.Logger, org.apache.logging.log4j.Logger, org.apache.log4j.Logger, ch.qos.logback.classic.Logger
     * @param serializableType 序列化类型
     * @return LazyLogger
     */
    public static LazyLogger getLogger(Object log
            , SerializableType serializableType) {
        return new LazyLogger(log, serializableType);
    }

    /**
     * fastjson序列化
     *
     * @param argument 参数
     * @return 序列化后的字符串
     */
    private String fastjsonSerialize(Object argument) {
        try {
            return (String) fastjsonMethod.invoke(null, argument);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public LazyLogger summaryDisable() {
        this.supportSummary = false;
        return this;
    }

    /**
     * 反射调用方法
     *
     * @param method 方法
     * @param args   参数
     * @return 返回值
     */
    private Object invoke(Method method, Object... args) {
        try {
            return method.invoke(wrapped, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public LazyLogger summary(int countThreshold, Duration durationThreshold) {
        this.countThreshold = countThreshold;
        this.durationThreshold = Math.min((int) durationThreshold.toMillis(), (int) Duration.ofDays(1).toMillis());
        return this;
    }

    /**
     * 获取参数
     *
     * @param arguments 参数
     * @return 参数
     */
    private Object[] getArgs(Object... arguments) {
        if (arguments == null || arguments.length == 0) {
            return null;
        }
        Object[] newArgs = new Object[arguments.length];
        for (int i = 0; i < newArgs.length; i++) {
            Object argument = arguments[i];
            if (argument == null || argument instanceof String) {
                newArgs[i] = argument;
                continue;
            }

            Object realArgument = argument;
            if (argument instanceof LazyExecutor) {
                realArgument = ((LazyExecutor) argument).supplier.get();
            } else if (argument instanceof Supplier) {
                realArgument = ((Supplier<?>) argument).get();
            }

            if (realArgument == null
                    || realArgument instanceof String
                    || realArgument instanceof Throwable) {
                newArgs[i] = realArgument;
                continue;
            }

            if (SerializableType.FASTJSON.equals(serializableType)) {
                newArgs[i] = fastjsonSerialize(realArgument);
            } else {
                newArgs[i] = realArgument;
            }
        }

        return newArgs;
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     * @since 1.4
     */
    public void trace(String format, Object... arguments) {
        if (!(Boolean) invoke(isTraceEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(traceMethod, getFormat(format, realArgs), realArgs);
    }

    private String getFormat(String format, Object[] realArgs) {
        if (!supportSummary
                || realArgs == null || realArgs.length == 0
                || format == null || format.isEmpty()) {
            return format;
        }

        Matcher matcher = pattern.matcher(format);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        if (count + 1 != realArgs.length
                || !(realArgs[realArgs.length - 1] instanceof Throwable)) {
            return format;
        }

        Throwable throwable = (Throwable) realArgs[realArgs.length - 1];
        realArgs[realArgs.length - 1] = null;
        return format + "\n" + SummaryExceptions.getFullStackTrace(throwable
                , wrapped
                , excludeClassList
                , countThreshold
                , durationThreshold);
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     * @since 1.4
     */
    public void trace(String format, Supplier<?>... arguments) {
        if (!(Boolean) invoke(isTraceEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(traceMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void debug(String format, Object... arguments) {
        if (!(Boolean) invoke(isDebugEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(debugMethod, getFormat(format, realArgs), realArgs);
    }


    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void debug(String format, Supplier<?>... arguments) {
        if (!(Boolean) invoke(isDebugEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(debugMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void info(String format, Object... arguments) {
        if (!(Boolean) invoke(isInfoEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(infoMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void info(String format, Supplier<?>... arguments) {
        if (!(Boolean) invoke(isInfoEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(infoMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void warn(String format, Object... arguments) {
        if (!(Boolean) invoke(isWarnEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(warnMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void warn(String format, Supplier<?>... arguments) {
        if (!(Boolean) invoke(isWarnEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(warnMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void error(String format, Object... arguments) {
        if (!(Boolean) invoke(isErrorEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(errorMethod, getFormat(format, realArgs), realArgs);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void error(String format, Supplier<?>... arguments) {
        if (!(Boolean) invoke(isErrorEnabledMethod)) {
            return;
        }
        Object[] realArgs = getArgs(arguments);
        invoke(errorMethod, getFormat(format, realArgs), realArgs);
    }


    public enum SerializableType {
        FASTJSON, RAW
    }

    /**
     * 延迟执行
     *
     * @param <T> 返回值类型
     */
    public static class LazyExecutor<T> {
        /**
         * 延迟执行
         */
        private final Supplier<T> supplier;

        /**
         * 构造方法隐藏
         */
        LazyExecutor(Supplier<T> supplier) {
            this.supplier = supplier;
        }
    }


}
