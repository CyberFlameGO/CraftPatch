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
            new MethodTransform(method -> true)
                .replace("this.code = 4567;")
        );

        ReplaceClass instance = new ReplaceClass();
        instance.method();

        assertEquals("Method body must have been replaced", 4567, instance.code);
    }

    @Test
    public void testLineInsertion() {


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
}
