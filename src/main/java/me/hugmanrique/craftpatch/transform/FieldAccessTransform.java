package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.FieldAccess;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class FieldAccessTransform extends ExprReplacementTransform<FieldAccess> {
    public FieldAccessTransform(Predicate<FieldAccess> filter) {
        super(FieldAccess.class, filter);
    }

    public FieldAccessTransform() {
        this(null);
    }

    @Override
    protected void modify(FieldAccess target) throws CannotCompileException {
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed();";
    }

    public FieldAccessTransform setResult(String result) {
        replace("$_ = " + result + ";");
        return this;
    }
}
