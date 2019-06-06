package me.hugmanrique.craftpatch.patch;

import me.hugmanrique.craftpatch.Patch;
import me.hugmanrique.craftpatch.Transformation;

import java.util.*;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public abstract class AbstractPatch implements Patch {
    protected final String target;

    protected final String methodName;
    protected final String methodDescription;
    protected final Class<?>[] methodParamTypes;
    protected final String[] methodParamClassNames;

    protected final List<Transformation> transformations;

    private AbstractPatch(String target, String methodName, String methodDescription, Class<?>[] methodParamTypes, String[] methodParamClassNames) {
        this.target = Objects.requireNonNull(target);
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.methodParamTypes = methodParamTypes;
        this.methodParamClassNames = methodParamClassNames;
        this.transformations = new ArrayList<>();
    }

    protected AbstractPatch(String target, String methodName) {
        this(target, methodName, null, null, null);
    }

    public AbstractPatch(String target, String methodName, String methodDescription) {
        this(target, methodName, methodDescription, null, null);
    }

    public AbstractPatch(String target, String methodName, Class<?>[] methodParamTypes) {
        this(target, methodName, null, methodParamTypes, null);
    }

    public AbstractPatch(String target, String methodName, String[] methodParamClassNames) {
        this(target, methodName, null, null, methodParamClassNames);
    }

    @Override
    public String target() {
        return target;
    }

    @Override
    public String method() {
        return methodName;
    }

    @Override
    public String methodDescription() {
        return methodDescription;
    }

    @Override
    public Class<?>[] methodParams() {
        return methodParamTypes;
    }

    @Override
    public String[] methodParamClassNames() {
        return methodParamClassNames;
    }

    @Override
    public Collection<Transformation> transformations() {
        return transformations;
    }

    @Override
    public String toString() {
        return "AbstractPatch{" +
                "target='" + target + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodDescription='" + methodDescription + '\'' +
                ", methodParamTypes=" + Arrays.toString(methodParamTypes) +
                ", methodParamClassNames=" + Arrays.toString(methodParamClassNames) +
                '}';
    }
}
