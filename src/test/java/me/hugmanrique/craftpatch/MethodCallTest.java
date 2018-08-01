package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.MethodCallTransform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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



}
