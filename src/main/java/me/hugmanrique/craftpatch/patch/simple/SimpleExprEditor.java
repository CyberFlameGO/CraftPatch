package me.hugmanrique.craftpatch.patch.simple;

import javassist.CannotCompileException;
import javassist.expr.*;
import me.hugmanrique.craftpatch.Transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
@SuppressWarnings("unchecked")
class SimpleExprEditor extends ExprEditor {
    private static List<Transformation> getTransformations(Collection<Transformation> transformations, Class<?> type) {
        List<Transformation> filtered = new ArrayList<>();

        for (Transformation transformation : transformations) {
            if (transformation.getType().equals(type)) {
                filtered.add(transformation);
            }
        }

        return filtered;
    }

    private final List<Transformation> methodCallTransforms;
    private final List<Transformation> fieldAccessTransforms;
    private final List<Transformation> newExprTransforms;
    private final List<Transformation> newArrayTransforms;
    private final List<Transformation> instanceofTransforms;
    private final List<Transformation> castTransforms;
    private final List<Transformation> catchTransforms;

    SimpleExprEditor(Collection<Transformation> transformations) {
        methodCallTransforms = getTransformations(transformations, MethodCall.class);
        fieldAccessTransforms = getTransformations(transformations, FieldAccess.class);
        newExprTransforms = getTransformations(transformations, NewExpr.class);
        newArrayTransforms = getTransformations(transformations, NewArray.class);
        instanceofTransforms = getTransformations(transformations, Instanceof.class);
        castTransforms = getTransformations(transformations, Cast.class);
        catchTransforms = getTransformations(transformations, Handler.class);
    }

    @Override
    public void edit(MethodCall call) throws CannotCompileException {
        for (Transformation<MethodCall> transformation : methodCallTransforms) {
            transformation.apply(call);
        }
    }

    @Override
    public void edit(FieldAccess access) throws CannotCompileException {
        for (Transformation<FieldAccess> transformation : fieldAccessTransforms) {
            transformation.apply(access);
        }
    }

    @Override
    public void edit(NewExpr newExpr) throws CannotCompileException {
        for (Transformation<NewExpr> transformation : newExprTransforms) {
            transformation.apply(newExpr);
        }
    }

    @Override
    public void edit(NewArray newArray) throws CannotCompileException {
        for (Transformation<NewArray> transformation : newArrayTransforms) {
            transformation.apply(newArray);
        }
    }

    @Override
    public void edit(Instanceof instanceOf) throws CannotCompileException {
        for (Transformation<Instanceof> transformation : instanceofTransforms) {
            transformation.apply(instanceOf);
        }
    }

    @Override
    public void edit(Cast cast) throws CannotCompileException {
        for (Transformation<Cast> transformation : castTransforms) {
            transformation.apply(cast);
        }
    }

    @Override
    public void edit(Handler handler) throws CannotCompileException {
        for (Transformation<Handler> transformation : catchTransforms) {
            transformation.apply(handler);
        }
    }
}
