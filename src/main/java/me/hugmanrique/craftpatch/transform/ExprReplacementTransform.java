package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.Expr;
import me.hugmanrique.craftpatch.util.StatementUtil;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import static me.hugmanrique.craftpatch.util.StatementUtil.checkStatement;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public abstract class ExprReplacementTransform<T extends Expr> extends AbstractTransform<T> {
    protected String before = "";
    private String statement;
    protected String after = "";

    public ExprReplacementTransform(Class<T> type, Predicate<T> filter) {
        super(type, filter);
    }

    @Override
    public void apply(T target) throws CannotCompileException {
        if (!isNullStatementAllowed() && statement == null) {
            throw new NullPointerException("Transformation doesn't support null statement replacements");
        }

        if (shouldApply(target)) {
            modify(target);
        }
    }

    protected abstract String getDefault();

    protected boolean isNullStatementAllowed() {
        return false;
    }

    protected abstract void modify(T target) throws CannotCompileException;

    protected String getStatement() {
        return "{" + before + (statement == null ? getDefault() : statement) + after + "}";
    }

    protected String getRawStatement() {
        return statement;
    }

    public ExprReplacementTransform<T> replace(String statement) {
        this.statement = checkStatement(statement);
        return this;
    }

    public ExprReplacementTransform<T> prepend(String statement) {
        before += checkStatement(statement);
        return this;
    }

    public ExprReplacementTransform<T> append(String statement) {
        after += checkStatement(statement);
        return this;
    }

    public ExprReplacementTransform<T> callBefore(Method method, String arguments) {
        String statement = StatementUtil.generateMethodInvocation(method, arguments);
        prepend(statement);

        return this;
    }

    public ExprReplacementTransform<T> callAfter(Method method, String arguments) {
        String statement = StatementUtil.generateMethodInvocation(method, arguments);
        append(statement);

        return this;
    }
}
