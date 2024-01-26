package io.github.onetool4j.util;

import java.util.*;

/**
 * 2024/1/7 15:50
 * 支持摘要的异常类
 *
 * @author admin
 */
public abstract class SummaryExceptions extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

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
    private static final Map<SummaryKey, SummaryStats> stack_traces = new LinkedHashMap<SummaryKey, SummaryStats>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<SummaryKey, SummaryStats> eldest) {
            return size() > Math.min(Integer.parseInt(System.getProperty("exception.supportSummary.stack.cache.threshold", "2000")), 2000);
        }
    };

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
     *
     */
    public static String getFullStackTrace(Throwable throwable, Object lock, Set<String> excludeClassList, int countThreshold, int durationThreshold) {
        if (throwable == null || lock == null) {
            return "";
        }

        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
        dejaVu.add(throwable);
        StringBuilder builder = new StringBuilder();

        synchronized (lock) {
            SummaryKey summaryKey = getSummaryKey(throwable, excludeClassList);
            SummaryStats mergeStats = stack_traces.get(summaryKey);
            if (mergeStats == null
                    || mergeStats.count >= countThreshold
                    || (System.currentTimeMillis() - mergeStats.timestamp) > durationThreshold) {
                mergeStats = new SummaryStats(generateRandomString(16));
                stack_traces.put(summaryKey, mergeStats);

                // Print our stack traceElements
                builder.append(throwable).append("\n");
                builder.append("\tsummary exception search key[").append(mergeStats.uuid).append("] ").append(countThreshold).append("times/").append(durationThreshold / 1000).append("s").append("\n");
                StackTraceElement[] traceElements = throwable.getStackTrace();

                for (StackTraceElement traceElement : traceElements) {
                    if (!excludeClassList.contains(traceElement.getClassName())) {
                        builder.append("\tat ").append(traceElement).append("\n");
                    }
                }

                // Print suppressed exceptions, if any
                for (Throwable se : throwable.getSuppressed()) {
                    appendEnclosedStackTrace(builder, se, lock, traceElements, SUPPRESSED_CAPTION, "\t", dejaVu, excludeClassList);
                }


                // Print cause, if any
                Throwable ourCause = throwable.getCause();
                if (ourCause != null) {
                    appendEnclosedStackTrace(builder, ourCause, lock, traceElements, CAUSE_CAPTION, "", dejaVu, excludeClassList);
                }
            } else {
                mergeStats.increment();
                if (mergeStats.summaryMessage == null) {
                    mergeStats.summaryMessage = throwable + "\n" +
                            "\tsummary exception search key[" + mergeStats.uuid + "] " + countThreshold + "times/" + durationThreshold / 1000 + "s" + "\n";
                }
                builder.append(mergeStats.summaryMessage);
            }
        }

        return builder.toString();
    }

    /**
     * 打印异常堆栈
     *
     * @param builder        StringBuilder
     * @param se             Throwable
     * @param lock           Object
     * @param enclosingTrace StackTraceElement[]
     * @param caption        String
     * @param prefix         String
     * @param dejaVu         throwable set
     */
    private static void appendEnclosedStackTrace(StringBuilder builder, Throwable se, Object lock, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu, Set<String> excludeClassList) {
        assert Thread.holdsLock(lock);
        if (dejaVu.contains(se)) {
            builder.append("\t[CIRCULAR REFERENCE:").append(se).append("]").append("\n");
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
            builder.append(prefix).append(caption).append(se).append("\n");
            for (int i = 0; i <= m; i++) {
                StackTraceElement traceElement = traceElements[i];
                if (!excludeClassList.contains(traceElement.getClassName())) {
                    builder.append(prefix).append("\tat ").append(traceElement).append("\n");
                }

            }

            if (framesInCommon != 0) {
                builder.append(prefix).append("\t... ").append(framesInCommon).append(" more").append("\n");
            }


            // Print suppressed exceptions, if any
            for (Throwable suppressed : se.getSuppressed()) {
                appendEnclosedStackTrace(builder, suppressed, lock, traceElements, SUPPRESSED_CAPTION, prefix + "\t", dejaVu, excludeClassList);
            }


            // Print cause, if any
            Throwable ourCause = se.getCause();
            if (ourCause != null) {
                appendEnclosedStackTrace(builder, ourCause, lock, traceElements, CAUSE_CAPTION, prefix, dejaVu, excludeClassList);
            }

        }
    }


    /**
     * 构建异常堆栈
     *
     * @param e BaseException
     */
    private static SummaryKey getSummaryKey(Throwable e, Set<String> excludeClassList) {

        List<Throwable> throwables = new ArrayList<>();
        Throwable cause = e;
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
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (!excludeClassList.contains(stackTraceElement.getClassName())) {
                    stack.className = stackTraceElement.getClassName();
                    stack.methodName = stackTraceElement.getMethodName();
                    stack.lineNumber = stackTraceElement.getLineNumber();
                    break;
                }
            }
        }

        return new SummaryKey(e, list);
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
    private static class SummaryStats {
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

        private String summaryMessage;

        /**
         * 构造方法
         *
         * @param uuid uuid
         */
        public SummaryStats(String uuid) {
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

    /**
     * MergeStats
     */
    private static class SummaryKey {
        public Throwable throwable;

        public List<StackTrace> stackTraceList;

        public SummaryKey(Throwable throwable, List<StackTrace> stackTraceList) {
            this.throwable = throwable;
            this.stackTraceList = stackTraceList;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof SummaryKey)) {
                return false;
            }
            SummaryKey other = (SummaryKey) obj;
            if (this.throwable == other.throwable) {
                return true;
            }

            return Objects.equals(throwable, other.throwable)
                    || Objects.equals(stackTraceList, other.stackTraceList);
        }


        @Override
        public int hashCode() {
            return Objects.hash(stackTraceList);
        }
    }


}
