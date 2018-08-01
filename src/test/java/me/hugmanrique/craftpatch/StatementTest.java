package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.util.StatementUtil;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class StatementTest {
    // TODO Move this class to PatchTest or create static util
    private Method getMethod(String name, Class<?>... parameterTypes) {
        try {
            return getClass().getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void simpleMethod() {}

    public static void complexMethod(String value, int number, List<Object> objects) {}

    private static void privateMethod() {}

    public void nonStaticMethod() {}

    public static void otherMethod(int a, int b) {}

    @Test
    public void testSimpleMethodInvocation() {
        Method method = getMethod("simpleMethod");
        String statement = StatementUtil.generateMethodInvocation(method, "");

        assertEquals(getClass().getName() + ".simpleMethod();", statement);
    }

    @Test
    public void testComplexMethodInvocation() {
        Method method = getMethod("complexMethod", String.class, int.class, List.class);
        String statement = StatementUtil.generateMethodInvocation(method, "\"blah\", 1, new ArrayList<>()");

        assertEquals(getClass().getName() + ".complexMethod(\"blah\", 1, new ArrayList<>());", statement);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonPublicThrows() {
        Method method = getMethod("privateMethod");
        StatementUtil.generateMethodInvocation(method, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonStaticThrows() {
        Method method = getMethod("nonStaticMethod");
        StatementUtil.generateMethodInvocation(method, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void incorrectParameterCount() {
        Method method = getMethod("otherMethod", int.class, int.class);

        StatementUtil.generateMethodInvocation(method, "1, 2, 3");
    }
}
