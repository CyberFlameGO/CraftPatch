package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.MethodCall;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class MethodCallTransform extends ExprReplacementTransform<MethodCall> {
    public MethodCallTransform(Predicate<MethodCall> filter) {
        super(MethodCall.class, filter);
    }

    public MethodCallTransform() {
        this(null);
    }

    @Override
    protected void modify(MethodCall target) throws CannotCompileException {
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($$);";
    }

    public MethodCallTransform setArguments(String arguments) {
        replace(getDefault().replace("$$", arguments));
        return this;
    }
}
