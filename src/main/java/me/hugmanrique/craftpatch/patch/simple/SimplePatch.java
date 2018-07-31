package me.hugmanrique.craftpatch.patch.simple;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import me.hugmanrique.craftpatch.Transformation;
import me.hugmanrique.craftpatch.patch.AbstractPatch;

import java.util.Arrays;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class SimplePatch extends AbstractPatch {
    public SimplePatch(String target, String methodName) {
        super(target, methodName);
    }

    public SimplePatch(String target, String methodName, String methodDescription) {
        super(target, methodName, methodDescription);
    }

    public SimplePatch(String target, String methodName, Class<?>[] methodParamTypes) {
        super(target, methodName, methodParamTypes);
    }

    @Override
    public void transform(ClassPool pool, CtClass clazz, CtMethod method) throws CannotCompileException {
        SimpleExprEditor editor = new SimpleExprEditor(transformations());

        if (method == null) {
            clazz.instrument(editor);
        } else {
            method.instrument(editor);
        }
    }

    public SimplePatch addTransformation(Transformation transformation) {
        transformations.add(transformation);
        return this;
    }

    public SimplePatch addTransformations(Transformation... transformations) {
        this.transformations.addAll(Arrays.asList(transformations));
        return this;
    }
}
