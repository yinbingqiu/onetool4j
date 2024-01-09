package io.github.onetool4j.util;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LazyLoggerTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(LazyLoggerTest.class);
    private static final LazyLogger logger = LazyLogger.getLogger(log);
    private static final LazyLogger json = LazyLogger.getLogger(log, LazyLogger.SerializableType.FASTJSON);

    public void test() {
        logger.trace("test trace a={} b={} c={} "
                , () -> 1
                , () -> 2
                , () -> 3
                , () -> new IllegalArgumentException("test"));

        logger.trace("test trace a={} b={} c={}"
                , LazyLogger.lazyEval(() -> 1)
                , 2
                , 3
                , new IllegalArgumentException("test"));


        logger.debug("test debug a={} b={} c={} "
                , () -> 1
                , () -> 2
                , () -> 3
                , () -> new IllegalArgumentException("test"));

        logger.debug("test debug a={} b={} c={}"
                , LazyLogger.lazyEval(() -> 1)
                , 2
                , 3
                , new IllegalArgumentException("test"));


        logger.info("test info a={} b={} c={} "
                , () -> 1
                , () -> 2
                , () -> 3
                , () -> new IllegalArgumentException("test"));

        logger.info("test info a={} b={} c={}"
                , LazyLogger.lazyEval(() -> 1)
                , 2
                , 3
                , new IllegalArgumentException("test"));

        logger.warn("test warn a={} b={} c={} "
                , () -> 1
                , () -> 2
                , () -> 3
                , () -> new IllegalArgumentException("test"));

        logger.warn("test warn a={} b={} c={}"
                , LazyLogger.lazyEval(() -> 1)
                , 2
                , 3
                , new IllegalArgumentException("test"));

        logger.error("test error a={} b={} c={} "
                , () -> 1
                , () -> 2
                , () -> 3
                , () -> new IllegalArgumentException("test"));

        logger.error("test error a={} b={} c={}"
                , LazyLogger.lazyEval(() -> 1)
                , 2
                , 3
                , new IllegalArgumentException("test"));
    }


    public void test1() {
        logger.error("test error a={} b={} c={} innerClass={}"
                , () -> 1
                , () -> 2
                , () -> 3
                , InnerClass::new
                , IllegalArgumentException::new);


        json.error("test json error a={} b={} c={} innerClass={}"
                , () -> 1
                , () -> 2
                , () -> 3
                , InnerClass::new
                , IllegalArgumentException::new);
    }


    public static class InnerClass {
        private Integer a = 1;

        private String b = "2";

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }

}