package io.github.onetool4j.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2024/1/24 14:47
 * 反射工具类
 *
 * @author admin
 */
public class Reflections {

    private static final Map<Class<?>, List<Field>> all_fields_cache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Method>> all_methods_cache = new ConcurrentHashMap<>();


    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取类继承链路上的所有可见性方法
     *
     * @param className 类名
     * @return 方法
     */
    public static List<Method> getAllMethods(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return getAllMethods(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取类继承链路上的所有可见性方法
     *
     * @param clazz 类
     * @return 方法
     */
    public static List<Method> getAllMethods(Class<?> clazz) {


        return new ArrayList<>(all_methods_cache.computeIfAbsent(clazz
                , key -> {
                    List<Method> methodList = new ArrayList<>();
                    while (key != null) {
                        methodList.addAll(Arrays.asList(key.getDeclaredMethods()));
                        key = key.getSuperclass();
                    }
                    return methodList;
                }));
    }

    /**
     * 获取类继承链路上的所有可见性方法获取对象的指定方法
     *
     * @param obj 类对象
     * @return 方法
     */
    public static List<Method> getAllMethods(Object obj) {
        assert obj != null;
        return getAllMethods(obj.getClass());
    }


    /**
     * 获取类继承链路上的所有可见性方法获取对象的指定方法
     *
     * @param clazz 类
     * @param name  方法名
     * @return 方法
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        assert clazz != null;
        assert name != null && !Objects.equals("", name.trim());

        parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;

        Class<?>[] finalParameterTypes = parameterTypes;
        return getAllMethods(clazz).stream()
                .filter(method -> method.getName().equals(name))
                .filter(method -> Arrays.equals(method.getParameterTypes(), finalParameterTypes))
                .findFirst().orElse(null);
    }

    /**
     * 获取类继承链路上的所有可见性方法获取对象的指定方法
     *
     * @param obj  类对象
     * @param name 方法名
     * @return 方法
     */
    public static Method getMethod(Object obj, String name, Class<?>... parameterTypes) {
        assert obj != null;
        return getMethod(obj.getClass(), name, parameterTypes);
    }

    /**
     * 获取类继承链路上的所有可见性方法获取对象的指定方法
     *
     * @param className      类名
     * @param name           方法名
     * @param parameterTypes 参数类型
     * @return 方法
     */
    public static Method getMethod(String className, String name, Class<?>... parameterTypes) {
        assert className != null;
        try {
            return getMethod(Class.forName(className), name, parameterTypes);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取类继承链路上的所有可见性字段
     *
     * @param clazz 类
     * @return 字段
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        return new ArrayList<>(all_fields_cache.computeIfAbsent(clazz
                , key -> {
                    List<Field> fieldList = new ArrayList<>();
                    while (key != null) {
                        fieldList.addAll(Arrays.asList(key.getDeclaredFields()));
                        key = key.getSuperclass();
                    }
                    return fieldList;
                }));
    }

    /**
     * 获取类继承链路上的所有可见性字段
     *
     * @param obj 类对象
     * @return 字段
     */
    public static List<Field> getAllFields(Object obj) {
        assert obj != null;
        return getAllFields(obj.getClass());
    }

    /**
     * 获取类继承链路上的所有可见性字段获取对象的指定字段
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        assert clazz != null;
        assert fieldName != null && !Objects.equals("", fieldName.trim());

        return getAllFields(clazz).stream()
                .filter(field -> Objects.equals(field.getName(), fieldName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取类继承链路上的所有可见性字段获取对象的指定字段
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段
     */
    public static Field getField(Object obj, String fieldName) {
        assert obj != null;
        return getField(obj.getClass(), fieldName);
    }


}
