package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.method.MethodTransform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Hugo Manrique
 * @since 02/08/2018
 */
public class MethodTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullFilter() {
        applyPatch("DummyClass", "method", new MethodTransform(null));
    }

    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("DummyClass", "method", new MethodTransform(method -> true));
    }

    @Test
    public void testReplacement() {
        applyPatch(
            "ReplaceClass",
            "method",
            new MethodTransform(method -> method.getName().equals("method"))
                .replace("this.code = 4567;")
        );

        ReplaceClass instance = new ReplaceClass();
        instance.method();

        assertEquals("Method body must have been replaced", 4567, instance.code);
    }

    @Test
    public void testLineInsertion() {
        applyPatch(
        "LineInsertion",
            "method",
            new MethodTransform(method -> method.getName().equals("method"))
                .insertAt(2, "this.value++;")
        );

        LineInsertion instance = new LineInsertion();
        instance.method();

        // If line gets inserted after value *= 2, the result will be 3
        assertEquals("Statement must have been inserted at the correct line", 4, instance.value);
    }

    // Mock classes
    class DummyClass {
        void method() {}
    }

    class ReplaceClass {
        int code;

        void method() {
            code = 1234;
        }
    }

    class LineInsertion {
        int value = 0;

        void method() {
            value++; // 1
            value *= 2; // 2, will be 4 (once we insert value++; on previous line)
        }
    }
}
