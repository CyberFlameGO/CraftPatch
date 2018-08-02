package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.expr.NewExprTransform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Hugo Manrique
 * @since 02/08/2018
 */
public class NewExprTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("DummyClass", "method", new NewExprTransform());
    }

    @Test
    public void testResultOverride() {
        applyPatch(
            "ResultClass",
            "method",
            new NewExprTransform()
                .setResult("new me.hugmanrique.craftpatch.NewExprTest$OtherClass(2)")
        );

        ResultClass instance = new ResultClass();
        assertEquals("Overridden result must have changed", 2, instance.method().value);
    }

    @Test
    public void testNullResult() {
        applyPatch(
            "NullResult",
            "method",
            new NewExprTransform()
                .setResult("$0")
        );

        NullResult instance = new NullResult();
        assertNull("Overridden result must be null", instance.method());
    }

    // Mock classes
    class DummyClass {
        void method() {
            new Object();
        }
    }

    public static class OtherClass {
        final int value;

        public OtherClass(int value) {
            this.value = value;
        }
    }

    class ResultClass {
        OtherClass method() {
            return new OtherClass(1);
        }
    }

    class NullResult {
        Object method() {
            return new Object();
        }
    }
}
