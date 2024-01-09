package io.github.onetool4j.util;

import junit.framework.TestCase;

public class PredicatesTest extends TestCase {

    public void testCaseWhen() {

        System.out.println(Predicates
                .when(1 > 2).then(() -> 1)
                .when(3 > 2).then(() -> 2)
                .elseEval(Integer.class, () -> 3));

        System.out.println(Predicates
                .when(1 > 2).then(() -> 1)
                .when(3 > 2).then(() -> 2)
                .eval(Integer.class));

        Predicates
                .when(1 > 2).then(() -> System.out.println("1"))
                .when(3 > 2).then(() -> System.out.println("2"))
                .elseEval(() -> System.out.println("3"));

        Predicates
                .when(1 > 2).then(() -> System.out.println("1"))
                .when(3 > 2).then(() -> System.out.println("2"))
                .eval();


//        Object o = Predicates.when(1 > 2).then(() -> 1)
//                .when(3 > 2).then(() -> 2)
//                .elseCall(() -> 3);
//        System.out.println(o);
//
//        System.out.println(Predicates
//                .when(() -> 1 > 2).then(() -> 1)
//                .when(() -> 3 > 2).then(() -> 2)
//                .elseCall(() -> 3));


    }

}