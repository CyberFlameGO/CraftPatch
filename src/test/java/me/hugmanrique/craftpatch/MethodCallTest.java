package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.MethodCallTransform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class MethodCallTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("PlainClass", "method", new MethodCallTransform());
    }

    @Test
    public void testParameterReplace() {
        applyPatch(
            "ParameterClass",
            "method",
            new MethodCallTransform()
                .setParameters("\"def\", 456")
        );

        ParameterClass instance = new ParameterClass();
        instance.method();

        assertEquals("Overridden parameter must be set", instance.text, "def");
        assertEquals("Primitive overridden parameter must be set", instance.number, 456);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParameterReplace() {
        applyPatch(
            "InvalidParameterClass",
            "method",
            new MethodCallTransform()
                // The method expects one argument
                .setParameters("\"bar\", 123")
        );
    }

    @Test
    public void testFilter() {
        applyPatch(
            "FilterClass",
            "method",
            new MethodCallTransform(call -> call.getMethodName().equals("other"))
                .append("this.test = true;")
        );

        FilterClass instance = new FilterClass();
        instance.method();

        assertTrue("Filtered transformed method call must set field to true", instance.test);
    }


    // Mock classes
    class PlainClass {
        void method() {
            other();
        }

        void other() {}
    }

    class ParameterClass {
        private String text;
        private int number;

        void method() {
            other("abc", 123);
        }

        void other(String text, int number) {
            this.text = text;
            this.number = number;
        }
    }

    class InvalidParameterClass {
        void method() {
            other("foo");
        }

        void other(String text) {}
    }

    class FilterClass {
        boolean test = false;

        void method() {
            other();
        }

        void other() {}
    }
}
