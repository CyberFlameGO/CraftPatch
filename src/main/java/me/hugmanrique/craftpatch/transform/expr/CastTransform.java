package me.hugmanrique.craftpatch.transform.expr;

import javassist.CannotCompileException;
import javassist.expr.Cast;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class CastTransform extends ExprReplacementTransform<Cast> {
    public CastTransform(Predicate<Cast> filter) {
        super(Cast.class, filter);
    }

    public CastTransform() {
        this(null);
    }

    @Override
    protected void modify(Cast target) throws CannotCompileException {
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($1);";
    }

    public CastTransform setCastClass(Class<?> clazz) {
        return setCastClass(clazz.getName());
    }

    public CastTransform setCastClass(String className) {
        replace("$_ = (" + className + ") $1;");
        return this;
    }

    public CastTransform setObject(String objectDef) {
        replace(getDefault().replace("$1", objectDef));
        return this;
    }
}
