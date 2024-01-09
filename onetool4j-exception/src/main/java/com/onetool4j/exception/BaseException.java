package com.onetool4j.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author admin
 * @description 基础异常类，只定义结构，不对外暴露能力
 * @date 2024/1/7 15:50
 */
class BaseException extends RuntimeException {
    /**
     * 异常栈合并缓存阈值，防OOM
     */
    private static final int STACK_CACHE_THRESHOLD = Integer.parseInt(System.getProperty("exception.merge.stack.cache.threshold", "2000"));
    private static final long serialVersionUID = 1L;
    /**
     * 异常合并时间阈值，防止异常合并过多，导致无法定位问题
     */
    private static final int MERGE_DURATION_THRESHOLD = Integer.parseInt(System.getProperty("exception.merge.seconds.threshold", "60")) * 1000;
    /**
     * 异常合并次数阈值，防止异常合并过多，导致无法定位问题
     */
    private static final int MERGE_COUNT_THRESHOLD = Integer.parseInt(System.getProperty("exception.merge.count.threshold", "1000"));

    /**
     * Caption  for labeling causative exception stack traces
     */
    private static final String CAUSE_CAPTION = "Caused by: ";

    /**
     * Caption for labeling suppressed exception stack traces
     */
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    // 异常合并缓存，简单lru cache
    private static final Map<BaseException, MergeStats> MERGE_CACHE = new LinkedHashMap<BaseException, MergeStats>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<BaseException, MergeStats> eldest) {
            return size() > STACK_CACHE_THRESHOLD;
        }
    };


    private String errorCode;
    private final boolean merge;


    BaseException(String errorCode, String errMessage, Throwable e, boolean merge) {
        super(errMessage, e);
        this.errorCode = errorCode;
        this.merge = merge;
    }

    public BaseException(ErrorCode errorCode, Throwable e, boolean merge) {
        this(errorCode.code, errorCode.message, e, merge);
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single
     * implementation of printStackTrace.
     */
    private abstract static class PrintStreamOrWriter {
        /**
         * Returns the object to be locked when using this StreamOrWriter
         */
        abstract Object lock();

        /**
         * Prints the specified string as a line on this StreamOrWriter
         */
        abstract void println(Object o);
    }

    private static class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        @Override
        Object lock() {
            return printStream;
        }

        @Override
        void println(Object o) {
            printStream.println(o);
        }
    }

    private static class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        @Override
        Object lock() {
            return printWriter;
        }

        @Override
        void println(Object o) {
            printWriter.println(o);
        }
    }


    @Override
    public void printStackTrace() {
        if (!merge) {
            super.printStackTrace();
        } else {
            printStackTrace(new WrappedPrintStream(System.out));
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (!merge) {
            super.printStackTrace(s);
        } else {
            printStackTrace(new WrappedPrintStream(s));
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (!merge) {
            super.printStackTrace(s);
        } else {
            printStackTrace(new WrappedPrintWriter(s));
        }
    }


    private static final List<String> exclude_keywords = Arrays.asList(BaseException.class.getName(), BizException.class.getName(), AssertionError.class.getName(), ErrorCode.class.getName());

    private void println(StringBuilder builder, String s) {
        if (s == null || s.trim().isEmpty()) {
            return;
        }

        if (exclude_keywords.stream().anyMatch(s::contains)) {
            return;
        }

        if (s.contains("com.shizhuang.security.exception.BizException.of")) {
            System.out.println(s);
        }
        builder.append(s).append("\n");
    }

    //    private void printStackTrace(PrintStreamOrWriter s) {
//        // Guard against malicious overrides of Throwable.equals by
//        // using a Set with identity equality semantics.
//        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
//        dejaVu.add(this);
//        StringBuilder builder = new StringBuilder();
//        try {
//            synchronized (s.lock()) {
//                // Print our stack trace
//                println(builder, this.toString());
//                StackTraceElement[] trace = getStackTrace();
//                for (StackTraceElement traceElement : trace) {
//                    println(builder, "\tat " + traceElement);
//                }
//
//                // Print suppressed exceptions, if any
//                for (Throwable se : getSuppressed()) {
//                    printEnclosedStackTrace(builder, se, s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);
//                }
//
//
//                // Print cause, if any
//                Throwable ourCause = getCause();
//                if (ourCause != null) {
//                    printEnclosedStackTrace(builder, ourCause, s, trace, CAUSE_CAPTION, "", dejaVu);
//                }
//
//            }
//        } finally {
//            s.println(builder.toString());
//        }
//    }
    private void printStackTrace(PrintStreamOrWriter s) {
        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
        dejaVu.add(this);
        StringBuilder builder = new StringBuilder();
        try {
            synchronized (s.lock()) {
                if (!merge) {
                    // Print our stack trace
                    println(builder, this.toString());
                    StackTraceElement[] trace = getStackTrace();
                    for (StackTraceElement traceElement : trace) {
                        println(builder, "\tat " + traceElement);
                    }

                    // Print suppressed exceptions, if any
                    for (Throwable se : getSuppressed()) {
                        printEnclosedStackTrace(builder, se, s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);
                    }


                    // Print cause, if any
                    Throwable ourCause = getCause();
                    if (ourCause != null) {
                        printEnclosedStackTrace(builder, ourCause, s, trace, CAUSE_CAPTION, "", dejaVu);
                    }


                } else {
                    MergeStats mergeStats = MERGE_CACHE.get(this);
                    if (mergeStats == null
                            || mergeStats.count >= MERGE_COUNT_THRESHOLD
                            || (System.currentTimeMillis() - mergeStats.timestamp) > MERGE_DURATION_THRESHOLD) {
                        mergeStats = new MergeStats(generateRandomString(16));
                        MERGE_CACHE.put(this, mergeStats);

                        // Print our stack trace
                        println(builder, this.toString());
                        println(builder, "\tmerged exception search key[" + mergeStats.uuid + "] " + MERGE_COUNT_THRESHOLD + "times/" + MERGE_DURATION_THRESHOLD / 1000 + "s");
                        StackTraceElement[] trace = getStackTrace();
                        for (StackTraceElement traceElement : trace) {
                            println(builder, "\tat " + traceElement);
                        }

                        // Print suppressed exceptions, if any
                        for (Throwable se : getSuppressed()) {
                            printEnclosedStackTrace(builder, se, s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);
                        }


                        // Print cause, if any
                        Throwable ourCause = getCause();
                        if (ourCause != null) {
                            printEnclosedStackTrace(builder, ourCause, s, trace, CAUSE_CAPTION, "", dejaVu);
                        }
                    } else {
                        mergeStats.increment();
                        println(builder, this.toString());
                        println(builder, "\tmerged exception search key[" + mergeStats.uuid + "] " + MERGE_COUNT_THRESHOLD + "times/" + MERGE_DURATION_THRESHOLD / 1000 + "s");
                    }
                }

            }
        } finally {
            s.println(builder.toString());
        }
    }

    private void printEnclosedStackTrace(StringBuilder builder, Throwable se, PrintStreamOrWriter s, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu) {
        assert Thread.holdsLock(s.lock());
        if (dejaVu.contains(se)) {
            println(builder, "\t[CIRCULAR REFERENCE:" + se + "]");
        } else {
            dejaVu.add(se);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = se.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
                m--;
                n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            println(builder, prefix + caption + se);
            for (int i = 0; i <= m; i++) {
                println(builder, prefix + "\tat " + trace[i]);
            }

            if (framesInCommon != 0) {
                println(builder, prefix + "\t... " + framesInCommon + " more");
            }


            // Print suppressed exceptions, if any
            for (Throwable suppressed : se.getSuppressed()) {
                printEnclosedStackTrace(builder, suppressed, s, trace, SUPPRESSED_CAPTION, prefix + "\t", dejaVu);
            }


            // Print cause, if any
            Throwable ourCause = se.getCause();
            if (ourCause != null) {
                printEnclosedStackTrace(builder, ourCause, s, trace, CAUSE_CAPTION, prefix, dejaVu);
            }

        }
    }


    public String getErrorCode() {
        return errorCode;
    }




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

    @Override
    public String getMessage() {
        return "业务异常: " + super.getMessage() + "[" + errorCode + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseException other = (BaseException) obj;
        // 判断两个异常是否相等，如果两个异常的errCode相等，则认为两个异常相等
        return recursiveEqual(this, other);
    }

    private boolean recursiveEqual(Throwable cause1, Throwable cause2) {
        if ((cause1 != null && cause2 == null)
                || (cause1 == null && cause2 != null)) {
            return false;
        }

        if (cause1 == null && cause2 == null) {
            return true;
        }

        return Objects.equals(cause1.toString(), cause2.toString())
                && recursiveEqual(cause1.getCause(), cause2.getCause());
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, getMessage());
    }

    @Override
    public String toString() {
        return getMessage();
    }

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

        public MergeStats(String uuid) {
            this.timestamp = System.currentTimeMillis();
            this.count = 1;
            this.uuid = uuid;
        }

        public void increment() {
            this.count++;
        }


    }
}
