package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.CatchTransform;
import org.junit.Test;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class CatchTest extends PatchTest {
    @Test
    public void testNullStatement() {
        applyPatch("DummyClass", "method", new CatchTransform());
    }

    // We're replacing the IllegalArgumentException with an IllegalStateException
    @Test(expected = IllegalStateException.class)
    public void testExceptionReplacement() {
        applyPatch(
            "ExceptionReplacement",
            "method",
            new CatchTransform()
                .setException("new java.lang.IllegalStateException")
        );

        ExceptionReplacement instance = new ExceptionReplacement();
        instance.method();
    }

    // Mock classes
    class DummyClass {
        void method() {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ExceptionReplacement {
        void method() {
            try {
                throw new IllegalArgumentException();
            } catch (Exception ignored) {}
        }
    }
}
