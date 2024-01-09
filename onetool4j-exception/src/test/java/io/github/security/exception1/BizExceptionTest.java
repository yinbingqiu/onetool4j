package io.github.security.exception1;

import io.github.onetool4j.exception.BizException;
import io.github.onetool4j.exception.ErrorCode;
import io.github.onetool4j.util.LazyLogger;
import junit.framework.TestCase;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Random;

public class BizExceptionTest extends TestCase {
    LazyLogger logger = LazyLogger.getLogger(LoggerFactory.getLogger(BizExceptionTest.class));


    public void testLogPrintStack() {

        try {
            mergeBizException();
        } catch (Exception e) {
            logger.info("test info abc a={} b={} c={} "
                    , () -> 1
                    , () -> 2
                    , () -> 3
                    , () -> e);
        }
    }

    private void mergeBizException() {

        try {
            inner1();
        } catch (Exception e) {
            throw BizException.ofMerge(ErrorCode.ofFail("json 解析异常"), e)
                    .mergeThreshold(11, Duration.ofMinutes(1));
        }
    }

    public void testBatchBizException() {
        System.setProperty("exception.merge.count.threshold", "10");

        for (int i = 0; i < 100; i++) {
            try {
                mergeBizException();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    private void inner1() {
        try {
            inner();
        } catch (Exception e) {
            throw BizException.of(ErrorCode.ofFail("包装的 json 异常"), e);
        }
    }

    private void inner() {
        int i = new Random().nextInt(100);
        if (i % 3 == 0) {
            System.out.println(1 / 0);
        } else if (i % 3 == 1) {
            throw new IllegalArgumentException("参数异常");
        } else {
            throw new RuntimeException("运行时异常");
        }

    }
}