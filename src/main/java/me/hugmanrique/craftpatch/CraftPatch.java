package me.hugmanrique.craftpatch;

import javassist.*;
import me.hugmanrique.craftpatch.util.ClassUtil;

import java.util.Objects;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class CraftPatch {
    private final ClassPool classPool;

    public CraftPatch(ClassPool classPool) {
        this.classPool = Objects.requireNonNull(classPool);
    }

    public CraftPatch() {
        this(ClassPool.getDefault());
    }

    public Class applyPatch(Patch patch) throws CannotCompileException {
        CtClass clazz = classPool.getOrNull(patch.target());

        if (clazz == null) {
            throw new NullPointerException("Cannot find " + patch.target() + " class");
        }

        clazz.defrost();
        CtMethod method;

        try {
            method = getMethod(patch, clazz);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }

        patch.transform(classPool, clazz, method);

        return clazz.toClass();
    }

    private CtMethod getMethod(Patch patch, CtClass clazz) throws NotFoundException {
        Class<?>[] methodParams = patch.methodParams();
        String methodName = patch.method();

        // Apply transformations to all the class methods
        if (methodName == null) {
            return null;
        }

        if (methodParams != null) {
            CtClass[] paramClasses = ClassUtil.toJavassistClasses(classPool, methodParams);
            return clazz.getDeclaredMethod(methodName, paramClasses);
        } else if (patch.methodDescription() != null) {
            return clazz.getMethod(methodName, patch.methodDescription());
        }

        return clazz.getDeclaredMethod(methodName);
    }
}
