package me.hugmanrique.craftpatch.transform.expr;

import javassist.expr.Expr;
import me.hugmanrique.craftpatch.transform.BaseTransform;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public abstract class ExprReplacementTransform<T extends Expr> extends BaseTransform<T> {
    public ExprReplacementTransform(Class<T> type, Predicate<T> filter) {
        super(type, filter);
    }
}
