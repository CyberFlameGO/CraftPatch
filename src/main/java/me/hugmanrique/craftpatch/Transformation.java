package me.hugmanrique.craftpatch;

import javassist.CannotCompileException;

import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public interface Transformation<T> {
    /**
     * Apply the transformation to the target object.
     * @param target the target object.
     * @throws CannotCompileException If the transformations generated illegal bytecode.
     */
    void apply(T target) throws CannotCompileException;

    /**
     * Discriminator for the target(s) to match, if not specified
     * then all targets matching the transformation type are matched.
     * @return the target filter.
     */
    Predicate<T> getFilter();

    default boolean shouldApply(T target) {
        return getFilter() == null || getFilter().test(target);
    }

    /**
     * Priority for the transformation, relative to other
     * transformations targetting the same classes/methods.
     */
    int priority();

    /**
     * The object's type this transformation targets.
     */
    Class<T> getType();
}
