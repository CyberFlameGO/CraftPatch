package me.hugmanrique.craftpatch.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class ClassUtil {
    private static Method GENERIC_SIGNATURE_METHOD;

    static {
        try {
            GENERIC_SIGNATURE_METHOD = Method.class.getDeclaredMethod("getGenericSignature", (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getMethodSignature(Method method) {
        try {
            return (String) GENERIC_SIGNATURE_METHOD.invoke(method, (Object[]) null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CtClass toJavassistClass(ClassPool pool, Class<?> clazz) {
        return pool.getOrNull(clazz.getName());
    }

    public static CtClass[] toJavassistClasses(ClassPool pool, Class<?>... classes) {
        return Arrays.stream(classes)
                .map(clazz -> toJavassistClass(pool, clazz))
                .toArray(CtClass[]::new);
    }

    public static CtClass[] toJavassistClasses(ClassPool pool, String... classNames) {
        return Arrays.stream(classNames)
                .map((Function<String, Object>) pool::getOrNull)
                .toArray(CtClass[]::new);
    }

    public static CtMethod toJavassistMethod(ClassPool pool, Method method) {
        CtClass clazz = toJavassistClass(pool, method.getDeclaringClass());
        String signature = getMethodSignature(method);

        if (signature == null) {
            return null;
        }

        try {
            return clazz.getMethod(method.getName(), signature);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getClassResourceName(Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }
}
