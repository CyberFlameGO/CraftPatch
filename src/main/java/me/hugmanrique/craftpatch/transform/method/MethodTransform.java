package me.hugmanrique.craftpatch.transform.method;

import javassist.CannotCompileException;
import javassist.CtMethod;
import me.hugmanrique.craftpatch.transform.BaseTransform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static me.hugmanrique.craftpatch.util.StatementUtil.checkStatement;

/**
 * @author Hugo Manrique
 * @since 02/08/2018
 */
public class MethodTransform extends BaseTransform<CtMethod> {
    private Map<Integer, String> insertions;

    public MethodTransform(Predicate<CtMethod> filter) {
        super(CtMethod.class, filter);
    }

    public MethodTransform() {
        this(null);
    }

    @Override
    protected void modify(CtMethod target) throws CannotCompileException {
        if (super.hasStatement()) {
            target.setBody(getStatement());
        }

        if (insertions != null) {
            for (Map.Entry<Integer, String> insertion : insertions.entrySet()) {
                target.insertAt(insertion.getKey(), true, insertion.getValue());
            }
        }
    }

    @Override
    protected String getDefault() {
        return "$_ = $proceed($$);";
    }

    @Override
    protected boolean hasStatement() {
        return (insertions != null && !insertions.isEmpty()) || super.hasStatement();
    }

    public MethodTransform insertAt(int line, String statement) {
        // Lazy map initialization
        if (insertions == null) {
            insertions = new HashMap<>();
        }

        insertions.put(line, checkStatement(statement));
        return this;
    }
}
