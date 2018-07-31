package me.hugmanrique.craftpatch.transform;

import me.hugmanrique.craftpatch.Transformation;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public abstract class AbstractTransform<T> implements Transformation<T> {
    private final Predicate<T> filter;
    private int priority = 1000;
    private final Class<T> type;

    public AbstractTransform(Class<T> type, Predicate<T> filter) {
        this.filter = filter;
        this.type = type;
    }

    public AbstractTransform(Class<T> type) {
        this(type, null);
    }

    @Override
    public Predicate<T> getFilter() {
        return filter;
    }

    @Override
    public int priority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
