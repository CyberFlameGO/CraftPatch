package me.hugmanrique.craftpatch.transform.method;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtMethod;
import me.hugmanrique.craftpatch.PatchApplier;
import me.hugmanrique.craftpatch.transform.AbstractTransform;
import me.hugmanrique.craftpatch.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Hugo Manrique
 * @since 02/08/2018
 */
public class CopyMethodTransform extends AbstractTransform<CtMethod> {
    private final CtMethod source;
    private final ClassMap classMap;

    public CopyMethodTransform(CtMethod source, ClassMap classMap, Predicate<CtMethod> filter) {
        super(CtMethod.class, filter);
        this.source = Objects.requireNonNull(source);
        this.classMap = Objects.requireNonNull(classMap);
    }

    public CopyMethodTransform(CtMethod source, Predicate<CtMethod> filter) {
        this(source, new ClassMap(), filter);
    }

    public CopyMethodTransform(PatchApplier applier, Method sourceMethod, ClassMap classMap, Predicate<CtMethod> filter) {
        this(ClassUtil.toJavassistMethod(applier.getPool(), sourceMethod), classMap, filter);
    }

    public CopyMethodTransform(PatchApplier applier, Method sourceMethod, Predicate<CtMethod> filter) {
        this(applier, sourceMethod, new ClassMap(), filter);
    }

    @Override
    public void apply(CtMethod target) throws CannotCompileException {
        target.setBody(source, classMap);
    }
}
