package io.github.onetool4j.util;

import junit.framework.TestCase;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class ReflectionsTest extends TestCase {

    public void testGetField() {
        List<Field> allFields = Reflections.getAllFields(HashMap.class);
        Field field = Reflections.getField(HashMap.class, "aaaa");
        System.out.println(allFields.size());
        List<Method> allMethods = Reflections.getAllMethods(LoggerFactory.class);
        System.out.println(allMethods);

    }

}