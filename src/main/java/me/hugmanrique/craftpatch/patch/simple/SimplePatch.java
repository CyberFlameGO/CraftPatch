package me.hugmanrique.craftpatch.patch.simple;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import me.hugmanrique.craftpatch.Transformation;
import me.hugmanrique.craftpatch.patch.AbstractPatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static me.hugmanrique.craftpatch.util.TransformationUtil.getTransformations;

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
        Collection<Transformation> transformations = transformations();
        SimpleExprEditor editor = new SimpleExprEditor(transformations);

        applyMethodTransforms(clazz, transformations);

        if (method == null) {
            clazz.instrument(editor);
        } else {
            method.instrument(editor);
        }
    }

    private void applyMethodTransforms(CtClass clazz, Collection<Transformation> transformations) throws CannotCompileException {
        List<Transformation> methodTransforms = getTransformations(transformations, CtMethod.class);

        if (methodTransforms.isEmpty()) {
            return;
        }

        for (CtMethod method : clazz.getMethods()) {
            for (Transformation<CtMethod> transformation : methodTransforms) {
                transformation.apply(method);
            }
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
