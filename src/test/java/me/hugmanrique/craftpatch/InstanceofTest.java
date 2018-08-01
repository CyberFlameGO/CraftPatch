package me.hugmanrique.craftpatch;

import javassist.NotFoundException;
import me.hugmanrique.craftpatch.transform.InstanceofTransform;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class InstanceofTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("PlainClass", "method", new InstanceofTransform());
    }

    @Test
    public void testPrepend() {
        applyPatch(
            "PrependClass",
            "method",
            new InstanceofTransform()
                .prepend("this.field = true;")
        );

        PrependClass instance = new PrependClass();
        instance.method();

        assertTrue("Prepend statement must set field to true", instance.field);
    }

    @Test
    public void testAppend() {
        applyPatch(
            "AppendClass",
            "method",
            new InstanceofTransform()
                .append("this.field = true;")
        );

        AppendClass instance = new AppendClass();
        instance.method();

        assertTrue("Append statement must set field to true", instance.field);
    }

    public static void callMethod(String value) {
        if (!"pass".equals(value)) {
            fail();
        }
    }

    @Test
    public void testMethodPrepend() {
        Method method = getMethod("callMethod", String.class);

        applyPatch(
            "MethodPrepend",
            "method",
            new InstanceofTransform()
                .callBefore(method, "\"pass\"")
        );

        MethodPrepend instance = new MethodPrepend();
        instance.method();
    }

    @Test
    public void testAlwaysFalse() {
        applyPatch(
            "AlwaysFalse",
            "method",
            new InstanceofTransform().alwaysFalse()
        );

        AlwaysFalse instance = new AlwaysFalse();
        assertFalse("Transformed instanceof must return false", instance.method());
    }

    @Test
    public void testAlwaysTrue() {
        applyPatch(
            "AlwaysTrue",
            "method",
            new InstanceofTransform().alwaysTrue()
        );

        AlwaysTrue instance = new AlwaysTrue();
        assertTrue("Transformed instanceof must return true", instance.method());
    }

    @Test
    public void testOverriddenClass() {
        applyPatch(
            "OverriddenClass",
            "method",
            new InstanceofTransform().instanceOf(String.class)
        );

        OverriddenClass instance = new OverriddenClass();
        assertFalse("Overridden instanceof class must return false", instance.method());
    }

    @Test
    public void testFilter() {
        applyPatch(
                "FilterClass",
                "method",
                new InstanceofTransform(anInstanceof -> {
                    try {
                        assertEquals(anInstanceof.getType().getName(), "java.lang.Object");
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }

                    return false;
                }).alwaysFalse()
        );

        FilterClass instance = new FilterClass();
        assertTrue("Non-transformed instanceof must return true", instance.method());
    }

    // Mock classes
    class PlainClass {
        boolean method() {
            return this instanceof Object;
        }
    }

    class PrependClass {
        boolean field = false;

        boolean method() {
            return this instanceof Object;
        }
    }

    class AppendClass {
        boolean field = false;

        boolean method() {
            return this instanceof Object;
        }
    }

    class MethodPrepend {
        boolean method() {
            return this instanceof Object;
        }
    }

    class AlwaysFalse {
        boolean method() {
            return this instanceof Object;
        }
    }

    class AlwaysTrue {
        boolean method() {
            return new Object() instanceof String;
        }
    }

    class OverriddenClass {
        boolean method() {
            return this instanceof Object;
        }
    }

    class FilterClass {
        boolean method() {
            return new Object() instanceof Object;
        }
    }
}
