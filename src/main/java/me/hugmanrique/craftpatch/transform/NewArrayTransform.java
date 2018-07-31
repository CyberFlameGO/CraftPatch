package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.NewArray;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class NewArrayTransform extends ExprReplacementTransform<NewArray> {
    public NewArrayTransform(Predicate<NewArray> filter) {
        super(NewArray.class, filter);
    }

    public NewArrayTransform() {
        this(null);
    }

    @Override
    protected void modify(NewArray target) throws CannotCompileException {
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($$);";
    }

    public NewArrayTransform setDimensions(int... dimensions) {
        if (dimensions.length <= 0) {
            throw new IllegalArgumentException("Invalid array dimensions: " + Arrays.toString(dimensions));
        }

        StringBuilder builder = new StringBuilder("$_ = $proceed(");

        for (int dim : dimensions) {
            builder.append(dim).append(",");
        }

        builder.setLength(builder.length() - 1);
        builder.append(");");

        replace(builder.toString());
        return this;
    }
}
