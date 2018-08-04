package me.hugmanrique.craftpatch;

import javassist.*;
import me.hugmanrique.craftpatch.util.ClassUtil;

import java.io.IOException;
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

    private CtClass transformTarget(Patch patch) throws CannotCompileException {
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

        return clazz;
    }

    public byte[] getBytecode(Patch patch) throws CannotCompileException, IOException {
        return Objects.requireNonNull(transformTarget(patch)).toBytecode();
    }

    public Class applyPatch(Patch patch) throws CannotCompileException {
        return Objects.requireNonNull(transformTarget(patch)).toClass();
    }

    private CtMethod getMethod(Patch patch, CtClass clazz) throws NotFoundException {
        String[] paramClassNames = patch.methodParamClassNames();
        Class<?>[] methodParams = patch.methodParams();
        String methodName = patch.method();

        // Apply transformations to all the class methods
        if (methodName == null) {
            return null;
        }

        if (paramClassNames != null) {
            CtClass[] paramClasses = ClassUtil.toJavassistClasses(classPool, paramClassNames);
            return clazz.getDeclaredMethod(methodName, paramClasses);
        } else if (methodParams != null) {
            CtClass[] paramClasses = ClassUtil.toJavassistClasses(classPool, methodParams);
            return clazz.getDeclaredMethod(methodName, paramClasses);
        } else if (patch.methodDescription() != null) {
            return clazz.getMethod(methodName, patch.methodDescription());
        }

        return clazz.getDeclaredMethod(methodName);
    }

    public ClassPool getPool() {
        return classPool;
    }
}
