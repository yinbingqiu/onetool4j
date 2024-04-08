package io.onetool4j.exception;


import junit.framework.TestCase;
import org.junit.Test;

public class AssertsTest extends TestCase {
    @Test
    public void test() {
        try {
            Asserts.isNull(new Object(), "a={} b={}", () -> "a", () -> 1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        try {
            Asserts.isNull(new Object(), "a=1 b=2");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}