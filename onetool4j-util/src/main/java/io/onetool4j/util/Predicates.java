package io.onetool4j.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 2024/1/24 15:49
 * Predicate 的扩展
 *
 * @author admin
 */
public class Predicates {
    /**
     * 用于开始一个when
     *
     * @param flag 条件
     * @param <T>  返回值类型
     * @return Predicate
     */
    public static <T> Predicate<T> when(boolean flag) {
        Predicate<T> predicate = new Predicate<>();
        predicate.list.add(new Stack<>(() -> flag));
        return predicate;
    }

    /**
     * 用于开始一个when
     *
     * @param flag 条件
     * @param <T>  返回值类型
     * @return Predicate
     */
    public static <T> Predicate<T> when(Callable<Boolean> flag) {
        Predicate<T> predicate = new Predicate<>();
        predicate.list.add(new Stack<>(flag));
        return predicate;
    }

    /**
     * Predicate
     *
     * @param <T> 返回值类型
     */
    public static class Predicate<T> {
        /**
         * 用于存储when的栈
         */
        private List<Stack<T>> list = new ArrayList<>();

        /**
         * 构造器
         */
        private Predicate() {
        }

        /**
         * 用于结束当前的when
         *
         * @param runnable runnable
         * @return Predicate
         */
        public Predicate<T> then(Runnable runnable) {
            this.list.get(this.list.size() - 1).runnable = runnable;
            return this;
        }

        /**
         * 用于结束当前的when
         *
         * @param callable callable
         * @return Predicate
         */
        public Predicate<T> then(Callable<T> callable) {
            this.list.get(this.list.size() - 1).callable = callable;
            return this;
        }

        /**
         * 用于结束当前的when
         *
         * @param flag flag
         * @return Predicate
         */
        public Predicate<T> when(boolean flag) {
            this.list.add(new Stack<>(() -> flag));
            return this;
        }

        /**
         * 用于结束当前的when
         *
         * @param flag flag
         * @return Predicate
         */
        public Predicate<T> when(Callable<Boolean> flag) {
            this.list.add(new Stack<>(flag));
            return this;
        }

        /**
         * 用于结束当前的when
         *
         * @param clazz    clazz
         * @param callable callable
         * @param <R>      返回值类型
         * @return R
         */
        public <R> R elseEval(Class<R> clazz, Callable<T> callable) {
            this.list.get(this.list.size() - 1).elseCallable = callable;
            return eval(clazz);
        }

        /**
         * 用于结束当前的when
         *
         * @param runnable runnable
         */
        public void elseEval(Runnable runnable) {
            this.list.get(this.list.size() - 1).elseRunnable = runnable;
            eval();
        }

        /**
         * 用于结束当前的when
         *
         * @param clazz clazz
         * @param <R>   返回值类型
         * @return R
         */
        public <R> R eval(Class<R> clazz) {
            try {
                for (Stack<T> stack : list) {
                    if (stack.predicate.call()) {
                        return (R) stack.callable.call();
                    }
                }
                Callable<T> lastStack = list.get(list.size() - 1).elseCallable;
                if (lastStack != null) {
                    return (R) lastStack.call();
                } else {
                    return null;
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        /**
         * 用于结束当前的when
         */
        public void eval() {
            try {
                for (Stack<T> stack : list) {
                    if (stack.predicate.call()) {
                        stack.runnable.run();
                    }
                }
                Runnable lastStack = list.get(list.size() - 1).elseRunnable;
                if (lastStack != null) {
                    lastStack.run();
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    /**
     * Stack
     *
     * @param <T> 返回值类型
     */
    public static class Stack<T> {
        /**
         * 条件
         */
        Callable<Boolean> predicate;
        /**
         * 条件为true时执行的方法
         */
        Runnable runnable;
        /**
         * 条件为false时执行的方法
         */
        Runnable elseRunnable;
        /**
         * 条件为true时执行的方法
         */
        Callable<T> callable;
        /**
         * 条件为false时执行的方法
         */
        Callable<T> elseCallable;

        /**
         * 构造器
         *
         * @param predicate 条件
         */
        private Stack(Callable<Boolean> predicate) {
            this.predicate = predicate;
        }

    }

}
