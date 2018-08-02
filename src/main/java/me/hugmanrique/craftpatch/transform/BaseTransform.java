package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import me.hugmanrique.craftpatch.util.StatementUtil;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import static me.hugmanrique.craftpatch.util.StatementUtil.checkStatement;

/**
 * @author Hugo Manrique
 * @since 02/08/2018
 */
public abstract class BaseTransform<T> extends AbstractTransform<T> {
    protected String before = "";
    private String statement;
    protected String after = "";

    public BaseTransform(Class<T> type, Predicate<T> filter) {
        super(type, filter);
    }

    @Override
    public void apply(T target) throws CannotCompileException {
        if (!allowNullStatements() && !hasStatement()) {
            throw new NullPointerException("Transformation doesn't support null statements replacements");
        }

        if (shouldApply(target)) {
            modify(target);
        }
    }

    protected abstract String getDefault();

    protected boolean allowNullStatements() {
        return false;
    }

    protected abstract void modify(T target) throws CannotCompileException;

    protected String getStatement() {
        return "{" + before + (statement == null ? getDefault() : statement) + after + "}";
    }

    protected String getRawStatement() {
        return statement;
    }

    protected boolean hasStatement() {
        return statement != null && !before.isEmpty() && !after.isEmpty();
    }

    // Transformer methods

    public BaseTransform<T> insert(InsertionType type, String statement) {
        switch (type) {
            case BEFORE:
                return prepend(statement);
            case REPLACE:
                return replace(statement);
            case AFTER:
                return append(statement);
            default:
                throw new AssertionError();
        }
    }

    public BaseTransform<T> replace(String statement) {
        this.statement = checkStatement(statement);
        return this;
    }

    public BaseTransform<T> prepend(String statement) {
        before += checkStatement(statement);
        return this;
    }

    public BaseTransform<T> append(String statement) {
        after += checkStatement(statement);
        return this;
    }

    public BaseTransform<T> prependCall(Method method, String parameters) {
        String statement = StatementUtil.generateMethodInvocation(method, parameters);
        return prepend(statement);
    }

    public BaseTransform<T> appendCall(Method method, String parameters) {
        String statement = StatementUtil.generateMethodInvocation(method, parameters);
        return append(statement);
    }

    public BaseTransform<T> debug() {
        System.out.println(getStatement());
        return this;
    }
}
