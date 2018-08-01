package me.hugmanrique.craftpatch.transform;

import javassist.CannotCompileException;
import javassist.expr.Handler;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class CatchTransform extends ExprReplacementTransform<Handler> {
    public CatchTransform(Predicate<Handler> filter) {
        super(Handler.class, filter);
    }

    public CatchTransform() {
        this(null);
    }

    @Override
    protected void modify(Handler target) throws CannotCompileException {
        boolean hasReplacement = getRawStatement() != null;
        boolean hasPrepend = !before.isEmpty();

        if (!hasReplacement && !hasPrepend) {
            throw new NullPointerException("Catch transformation must have appended content or a body replacement");
        }

        if (hasReplacement) {
            target.replace(getReplacement());
        }

        if (hasPrepend) {
            target.insertBefore(before);
        }
    }

    private String getReplacement() {
        return "{" + getRawStatement() + after + "}";
    }

    /*
     * Javassist doesn't support Handler#replace yet,
     * it does support #insertBefore though
     */
    @Override
    public ExprReplacementTransform<Handler> replace(String statement) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ExprReplacementTransform<Handler> append(String statement) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected String getDefault() {
        return null;
    }

    @Override
    protected boolean isNullStatementAllowed() {
        return true;
    }

    public CatchTransform setException(String exceptionDef) {
        replace("$1 = " + exceptionDef + ";");
        return this;
    }
}
