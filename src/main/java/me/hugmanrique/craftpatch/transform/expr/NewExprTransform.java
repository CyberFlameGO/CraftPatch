package me.hugmanrique.craftpatch.transform.expr;

import javassist.CannotCompileException;
import javassist.expr.NewExpr;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class NewExprTransform extends ExprReplacementTransform<NewExpr> {
    public NewExprTransform(Predicate<NewExpr> filter) {
        super(NewExpr.class, filter);
    }

    public NewExprTransform() {
        this(null);
    }

    @Override
    protected void modify(NewExpr target) throws CannotCompileException {
        target.replace(getStatement());
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($$);";
    }

    public NewExprTransform setResult(String newInstance) {
        replace("$_ = " + newInstance + ";");
        return this;
    }
}
