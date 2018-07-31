package me.hugmanrique.craftpatch.util;

import javassist.ClassPool;
import javassist.CtClass;

import java.util.Arrays;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class ClassUtil {
    public static CtClass toJavassistClass(ClassPool pool, Class<?> clazz) {
        return pool.getOrNull(clazz.getName());
    }

    public static CtClass[] toJavassistClasses(ClassPool pool, Class<?>... classes) {
        return Arrays.stream(classes)
                .map(clazz -> toJavassistClass(pool, clazz))
                .toArray(CtClass[]::new);
    }
}
