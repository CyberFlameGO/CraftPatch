package me.hugmanrique.craftpatch;

import javassist.CannotCompileException;
import me.hugmanrique.craftpatch.patch.simple.SimplePatch;

import java.lang.reflect.Method;

import static org.junit.Assert.fail;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class PatchTest {
    protected static final double DELTA = 0.00001; // For floating-point comparisons
    protected static final CraftPatch patcher = new CraftPatch();

    protected String getSubclassname(String className) {
        return getClass().getName() + "$" + className;
    }

    protected SimplePatch createPatch(String className, String methodName) {
        return new SimplePatch(getSubclassname(className), methodName);
    }

    protected SimplePatch applyPatch(String className, String methodName, Transformation... transformations) {
        SimplePatch patch = createPatch(className, methodName);

        patch.addTransformations(transformations);

        try {
            patcher.applyPatch(patch);
        } catch (CannotCompileException e) {
            e.printStackTrace();
            fail();
        }

        return patch;
    }

    protected Method getMethod(String name, Class<?>... parameterTypes) {
        try {
            return getClass().getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            fail();
        }

        return null;
    }
}
