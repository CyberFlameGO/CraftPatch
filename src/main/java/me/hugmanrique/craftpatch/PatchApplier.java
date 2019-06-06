package me.hugmanrique.craftpatch;

import javassist.*;
import me.hugmanrique.craftpatch.agent.InstrumentationFactory;
import me.hugmanrique.craftpatch.util.ClassUtil;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Objects;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class PatchApplier {
    private final ClassPool classPool;

    public PatchApplier(ClassPool classPool) {
        this.classPool = Objects.requireNonNull(classPool);
    }

    public PatchApplier() {
        this(ClassPool.getDefault());
    }

    private CtClass applyTransforms(Patch patch) throws PatchApplyException {
        CtClass clazz = classPool.getOrNull(patch.target());

        if (clazz == null) {
            throw new PatchApplyException("Cannot find " + patch.target() + " class");
        }

        clazz.defrost();
        CtMethod method;

        try {
            method = getMethod(patch, clazz);
        } catch (NotFoundException e) {
            throw new PatchApplyException(e);
        }

        try {
            patch.transform(classPool, clazz, method);
        } catch (CannotCompileException e) {
            throw new PatchApplyException(e);
        }

        return clazz;
    }

    public Class<?> applyPatch(Patch patch) throws PatchApplyException {
        return applyPatch(patch, false);
    }

    public Class<?> applyPatch(Patch patch, boolean redefine) throws PatchApplyException {
        CtClass clazz = applyTransforms(patch);

        if (redefine) {
            ClassDefinition definition = getDefinition(patch);

            if (definition == null) {
                throw new PatchApplyException("Class definition for patch" + patch + " was null");
            }

            try {
                Instrumentation instrumentation = InstrumentationFactory.getInstrumentation();

                if (instrumentation == null) {
                    throw new RuntimeException("Instrumentation instance is null");
                }

                instrumentation.redefineClasses(definition);
            } catch (Exception e) {
                throw new PatchApplyException("Could not grab an Instrumentation instance", e);
            }

            return definition.getDefinitionClass();
        }

        try {
            return clazz.toClass();
        } catch (CannotCompileException e) {
            throw new PatchApplyException(e);
        }
    }

    public byte[] getBytecode(Patch patch) throws PatchApplyException {
        try {
            return applyTransforms(patch).toBytecode();
        } catch (IOException | CannotCompileException e) {
            throw new PatchApplyException(e);
        }
    }

    public ClassDefinition getDefinition(Patch patch) throws PatchApplyException {
        try {
            Class clazz = Class.forName(patch.target());

            return new ClassDefinition(clazz, getBytecode(patch));
        } catch (ClassNotFoundException e) {
            throw new PatchApplyException(e);
        }
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
