package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.MethodCall;
import me.hugmanrique.craftpatch.util.StatementUtil;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class MethodCallTransform extends ExprReplacementTransform<MethodCall> {
    private String parameters;

    public MethodCallTransform(Predicate<MethodCall> filter) {
        super(MethodCall.class, filter);
    }

    public MethodCallTransform() {
        this(null);
    }

    @Override
    protected void modify(MethodCall target) throws CannotCompileException {
        StatementUtil.checkParameters(target, parameters);
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($$);";
    }

    public MethodCallTransform setParameters(String parameters) {
        replace(getDefault().replace("$$", parameters));
        this.parameters = parameters;
        return this;
    }
}
