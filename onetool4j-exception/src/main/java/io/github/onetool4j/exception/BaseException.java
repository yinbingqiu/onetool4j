package io.github.onetool4j.exception;

import io.github.onetool4j.util.LazyLogger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.*;

/**
 * 2024/1/7 15:50
 * 基础异常类，只定义结构，不对外暴露能力
 *
 * @author admin
 */
class BaseException extends RuntimeException {
    /**
     * 异常栈合并缓存阈值，防OOM
     */
    private static final int STACK_CACHE_THRESHOLD = Math.min(Integer.parseInt(System.getProperty("exception.merge.stack.cache.threshold", "2000")), 2000);
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 异常合并时间阈值，防止异常合并过多，导致无法定位问题
     */
    private static final int MERGE_DURATION_THRESHOLD = Math.min(Integer.parseInt(System.getProperty("exception.merge.seconds.threshold", "60")) * 1000, (int) Duration.ofDays(1).toMillis());
    /**
     * 异常合并次数阈值，防止异常合并过多，导致无法定位问题
     */
    private static final int MERGE_COUNT_THRESHOLD = Math.min(Integer.parseInt(System.getProperty("exception.merge.count.threshold", "1000")), 10000);

    /**
     * Caption  for labeling causative exception stack traces
     */
    private static final String CAUSE_CAPTION = "Caused by: ";

    /**
     * Caption for labeling suppressed exception stack traces
     */
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";
    /**
     * 用于生成随机字符串
     */
    private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /**
     * 异常合并缓存
     */
    private static final Map<BaseException, MergeStats> MERGE_CACHE = new LinkedHashMap<BaseException, MergeStats>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<BaseException, MergeStats> eldest) {
            return size() > STACK_CACHE_THRESHOLD;
        }
    };
    /**
     * 用于排除不需要打印的异常
     */
    private static final Set<String> EXCLUDE_KEYWORDS = new HashSet<>(Arrays.asList(
            BaseException.class.getName()
            , BizException.class.getName()
            , Asserts.class.getName()
            , ErrorCode.class.getName()
            , LazyLogger.class.getName()
    ));
    /**
     * 是否合并异常
     */
    private final boolean merge;
    /**
     * 错误码
     */
    private String code;
    /**
     * stackTraces
     */
    private List<StackTrace> stackTraceList;
    /**
     * 相对 originStackTraces 去掉了部分onetool异常堆栈，保持日志里面堆栈整洁
     */
    private StackTraceElement[] cachedStackTraces;
    /**
     * 原始堆栈
     */
    private StackTraceElement[] originStackTraces;
    /**
     * 异常合并次数阈值
     */
    private int mergetCountThreshold = MERGE_COUNT_THRESHOLD;
    /**
     * 异常合并时间阈值
     */
    private int mergeDurationThreshold = MERGE_DURATION_THRESHOLD;

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    /**
     * merge 阈值指定
     *
     * @param count    合并次数阈值
     * @param duration 合并时间阈值
     * @return BaseException
     */
    public BaseException mergeThreshold(int count, Duration duration) {
        if (!merge) {
            throw new IllegalArgumentException("当前异常不支持知道 merge 参数，请用ofMerge方法创建异常");
        }
        this.mergetCountThreshold = Math.min(count, 10000);
        this.mergeDurationThreshold = (int) Math.min(duration.toMillis(), Duration.ofDays(1).toMillis());
        return this;
    }

    /**
     * 打印异常堆栈
     */
    @Override
    public void printStackTrace() {
        if (!merge) {
            super.printStackTrace();
        } else {
            printStackTrace(new WrappedPrintStream(System.out));
        }
    }

    /**
     * 打印异常堆栈
     *
     * @param s PrintStreamOrWriter
     */
    @Override
    public void printStackTrace(PrintStream s) {
        if (!merge) {
            super.printStackTrace(s);
        } else {
            printStackTrace(new WrappedPrintStream(s));
        }
    }

    /**
     * 打印异常堆栈
     *
     * @param s PrintWriter
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        if (!merge) {
            super.printStackTrace(s);
        } else {
            printStackTrace(new WrappedPrintWriter(s));
        }
    }

    /**
     * 打印异常堆栈
     *
     * @param builder StringBuilder
     * @param s       打印字符串
     */
    private void println(StringBuilder builder, String s) {
        if (s == null || s.trim().isEmpty()) {
            return;
        }
        builder.append(s).append("\n");
    }

    /**
     * 构造方法
     *
     * @param code       错误码
     * @param errMessage 错误信息
     * @param e          异常
     * @param merge      是否合并异常
     */
    BaseException(String code, String errMessage, Throwable e, boolean merge) {
        super(errMessage, e);
        this.code = code;
        this.merge = merge;
    }
    /**
     * 构造方法
     *
     * @param code  错误码
     * @param e     异常
     * @param merge 是否合并异常
     */
    public BaseException(ErrorCode code, Throwable e, boolean merge) {
        this(code.code, code.message, e, merge);
    }

    public StackTraceElement[] getOriginStackTraces() {
        return originStackTraces;
    }

    /**
     * 获取堆栈，exclude 部分onetool异常堆栈
     *
     * @return StackTraceElement[]
     */
    @Override
    public StackTraceElement[] getStackTrace() {
        if (cachedStackTraces == null) {
            StackTraceElement[] stackTrace = super.getStackTrace();
            this.originStackTraces = stackTrace;
            cachedStackTraces = (stackTrace != null && stackTrace.length > 0)
                    ? Arrays.stream(stackTrace)
                    .filter(s -> !EXCLUDE_KEYWORDS.contains(s.getClassName()))
                    .toArray(StackTraceElement[]::new)
                    : new StackTraceElement[0];
        }
        return cachedStackTraces;
    }

    /**
     * 打印异常堆栈
     *
     * @param s PrintStreamOrWriter
     */
    private void printStackTrace(PrintStreamOrWriter s) {
        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
        dejaVu.add(this);
        StringBuilder builder = new StringBuilder();
        try {
            synchronized (s.lock()) {
                MergeStats mergeStats = MERGE_CACHE.get(this);
                if (mergeStats == null
                        || mergeStats.count >= MERGE_COUNT_THRESHOLD
                        || (System.currentTimeMillis() - mergeStats.timestamp) > MERGE_DURATION_THRESHOLD) {
                    mergeStats = new MergeStats(generateRandomString(16));
                    MERGE_CACHE.put(this, mergeStats);

                    // Print our stack traceElements
                    println(builder, this.toString());
                    println(builder, "\tmerged exception search key[" + mergeStats.uuid + "] " + MERGE_COUNT_THRESHOLD + "times/" + MERGE_DURATION_THRESHOLD / 1000 + "s");
                    StackTraceElement[] traceElements = getStackTrace();

                    for (StackTraceElement traceElement : traceElements) {
                        println(builder, "\tat " + traceElement);
                    }

                    // Print suppressed exceptions, if any
                    for (Throwable se : getSuppressed()) {
                        printEnclosedStackTrace(builder, se, s, traceElements, SUPPRESSED_CAPTION, "\t", dejaVu);
                    }


                    // Print cause, if any
                    Throwable ourCause = getCause();
                    if (ourCause != null) {
                        printEnclosedStackTrace(builder, ourCause, s, traceElements, CAUSE_CAPTION, "", dejaVu);
                    }
                } else {
                    mergeStats.increment();
                    println(builder, this.toString());
                    println(builder, "\tmerged exception search key[" + mergeStats.uuid + "] " + MERGE_COUNT_THRESHOLD + "times/" + MERGE_DURATION_THRESHOLD / 1000 + "s");
                }

            }
        } finally {
            s.println(builder.toString());
        }
    }

    /**
     * 打印异常堆栈
     *
     * @param builder        StringBuilder
     * @param se             Throwable
     * @param s              PrintStreamOrWriter
     * @param enclosingTrace StackTraceElement[]
     * @param caption        String
     * @param prefix         String
     * @param dejaVu         throwable set
     */
    private void printEnclosedStackTrace(StringBuilder builder, Throwable se, PrintStreamOrWriter s, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu) {
        assert Thread.holdsLock(s.lock());
        if (dejaVu.contains(se)) {
            println(builder, "\t[CIRCULAR REFERENCE:" + se + "]");
        } else {
            dejaVu.add(se);
            // Compute number of frames in common between this and enclosing traceElements
            StackTraceElement[] traceElements = se.getStackTrace();
            int m = traceElements.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >= 0 && traceElements[m].equals(enclosingTrace[n])) {
                m--;
                n--;
            }
            int framesInCommon = traceElements.length - 1 - m;

            // Print our stack traceElements
            println(builder, prefix + caption + se);
            for (int i = 0; i <= m; i++) {
                StackTraceElement traceElement = traceElements[i];
                println(builder, prefix + "\tat " + traceElement);
            }

            if (framesInCommon != 0) {
                println(builder, prefix + "\t... " + framesInCommon + " more");
            }


            // Print suppressed exceptions, if any
            for (Throwable suppressed : se.getSuppressed()) {
                printEnclosedStackTrace(builder, suppressed, s, traceElements, SUPPRESSED_CAPTION, prefix + "\t", dejaVu);
            }


            // Print cause, if any
            Throwable ourCause = se.getCause();
            if (ourCause != null) {
                printEnclosedStackTrace(builder, ourCause, s, traceElements, CAUSE_CAPTION, prefix, dejaVu);
            }

        }
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * message
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return this.getClass().getSimpleName() + " : " + super.getMessage() + "[" + code + "]";
    }

    /**
     * 判断两个异常是否相等
     *
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }


        BaseException other = (BaseException) obj;

        buildStackTraces(this);
        buildStackTraces(other);

        // 判断两个异常是否相等，如果两个异常的errCode相等，则认为两个异常相等
        return Objects.equals(stackTraceList, other.stackTraceList);
    }

    /**
     * 构建异常堆栈
     *
     * @param baseException BaseException
     */
    private void buildStackTraces(BaseException baseException) {
        if (baseException == null
                || baseException.stackTraceList != null) {
            return;
        }

        synchronized (baseException) {
            if (baseException.stackTraceList != null) {
                return;
            }


            List<Throwable> throwables = new ArrayList<>();
            Throwable cause = baseException;
            while (cause != null) {
                throwables.add(cause);
                cause = cause.getCause();
            }

            List<StackTrace> list = new ArrayList<>();
            for (Throwable throwable : throwables) {
                StackTraceElement[] stackTrace = throwable.getStackTrace();
                if (stackTrace == null || stackTrace.length == 0) {
                    continue;
                }
                StackTrace stack = new StackTrace();
                list.add(stack);
                if (throwable instanceof BaseException) {
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        stack.className = stackTraceElement.getClassName();
                        stack.methodName = stackTraceElement.getMethodName();
                        stack.lineNumber = stackTraceElement.getLineNumber();
                    }
                } else {

                    stack.className = stackTrace[0].getClassName();
                    stack.methodName = stackTrace[0].getMethodName();
                    stack.lineNumber = stackTrace[0].getLineNumber();

                }
            }

            baseException.stackTraceList = list;
        }
    }

    /**
     * hashCode
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, getMessage());
    }

    /**
     * toString
     *
     * @return String
     */
    @Override
    public String toString() {
        return getMessage();
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single
     * implementation of printStackTrace.
     */
    private abstract static class PrintStreamOrWriter {
        /**
         * 获取锁
         *
         * @return 锁
         */
        abstract Object lock();

        /**
         * 打印
         *
         * @param o 打印对象
         */
        abstract void println(Object o);
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single
     * implementation of printStackTrace.
     */
    private static class WrappedPrintStream extends PrintStreamOrWriter {
        /**
         * PrintStream
         */
        private final PrintStream printStream;

        /**
         * 构造方法
         *
         * @param printStream PrintStream
         */
        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        /**
         * 获取锁
         *
         * @return 锁
         */
        @Override
        Object lock() {
            return printStream;
        }

        /**
         * 打印
         *
         * @param o 打印对象
         */
        @Override
        void println(Object o) {
            printStream.println(o);
        }
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single
     * implementation of printStackTrace.
     */
    private static class WrappedPrintWriter extends PrintStreamOrWriter {
        /**
         * PrintWriter
         */
        private final PrintWriter printWriter;

        /**
         * 构造方法
         *
         * @param printWriter PrintWriter
         */
        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        /**
         * 获取锁
         *
         * @return 锁
         */
        @Override
        Object lock() {
            return printWriter;
        }

        /**
         * 打印
         *
         * @param o 打印对象
         */
        @Override
        void println(Object o) {
            printWriter.println(o);
        }
    }

    /**
     * StackTrace
     */
    static class StackTrace {
        /**
         * className
         */
        private String className;
        /**
         * methodName
         */
        private String methodName;
        /**
         * lineNumber
         */
        private int lineNumber;

        /**
         * hashCode
         */
        @Override
        public int hashCode() {
            return Objects.hash(className, methodName, lineNumber);
        }

        /**
         * equals
         *
         * @param obj Object
         * @return boolean
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            StackTrace other = (StackTrace) obj;
            return Objects.equals(className, other.className)
                    && Objects.equals(methodName, other.methodName)
                    && Objects.equals(lineNumber, other.lineNumber);
        }
    }

    /**
     * MergeStats
     */
    private static class MergeStats {
        /**
         * 合并时间戳
         */
        private long timestamp;
        /**
         * 合并次数
         */
        private int count;
        /**
         * 用于标识合并的uuid
         */
        private String uuid;

        /**
         * 构造方法
         *
         * @param uuid uuid
         */
        public MergeStats(String uuid) {
            this.timestamp = System.currentTimeMillis();
            this.count = 1;
            this.uuid = uuid;
        }

        /**
         * increment
         */
        public void increment() {
            this.count++;
        }
    }
}
