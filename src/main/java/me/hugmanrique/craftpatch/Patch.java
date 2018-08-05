package me.hugmanrique.craftpatch;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.Collection;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public interface Patch {
    /**
     * Target class for this patch. Referencing an already loaded class
     * e.g. by using {@link Class#getName()} using this property is an error
     * condition and will throw an exception at runtime.
     * @return class this patch targets.
     */
    String target();

    /**
     * Target method for this patch. If {@code null}, the transformations
     * will be applied to all the methods declared in the target class.
     * The method may be declared in a super class.
     * @return method's name this patch targets.
     */
    String method();

    /**
     * Method descriptor (as defined in the JVM specification) this patch
     * targets. If {@code null}, we will try to find a method with the {@link #method()}
     * name, but uniqueness isn't guaranteed.
     * @return method's signature this patch targets.
     */
    String methodDescription();

    /**
     * Parameter types of the target method of this patch. If {@code null}, will
     * check {@link #methodDescription()} instead. Only methods declared in the class
     * will be returned (by using {@link CtClass#getDeclaredMethod(String, CtClass[])}}).
     * @return method's parameter types this patch targets.
     */
    default Class<?>[] methodParams() {
        return null;
    }

    /**
     * Parameter types class names of the target method of this patch. If {@code null},
     * will check {@link #methodParams()} instead. Only methods declared in the class
     * will be returned (by using {@link CtClass#getDeclaredMethod(String, CtClass[])}).
     * @return method's parameter type class names this patch targets.
     */
    default String[] methodParamClassNames() {
        return null;
    }

    /**
     * Collection of all the transformations that will get applied to the
     * target class, and if not {@code null}, the the target method.
     */
    Collection<Transformation> transformations();

    /**
     * Applies all the transformations set on {@link #transformations()}
     * @param pool A class pool to load additional classes.
     * @param clazz The target resolved class.
     * @param method The target resolved method, might be {@code null}.
     * @throws CannotCompileException If the transformations generated illegal bytecode.
     */
    void transform(ClassPool pool, CtClass clazz, CtMethod method) throws CannotCompileException;
}
