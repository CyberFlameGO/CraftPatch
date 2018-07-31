package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.Instanceof;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class InstanceofTransform extends ExprReplacementTransform<Instanceof> {
    public InstanceofTransform(Predicate<Instanceof> filter) {
        super(Instanceof.class, filter);
    }

    public InstanceofTransform() {
        this(null);
    }

    @Override
    protected void modify(Instanceof target) throws CannotCompileException {
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($1);";
    }

    public InstanceofTransform instanceOf(Class<?> clazz) {
        return instanceOf(clazz.getName());
    }

    public InstanceofTransform instanceOf(String className) {
        replace("$_ = $1 instanceof " + className + ";");
        return this;
    }

    public InstanceofTransform alwaysTrue() {
        replace("$_ = true;");
        return this;
    }

    public InstanceofTransform alwaysFalse() {
        replace("$_ = false;");
        return this;
    }
}
