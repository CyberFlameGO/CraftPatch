package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.CatchTransform;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class CatchTest extends PatchTest {
    @Test
    public void testNullStatement() {
        applyPatch("DummyClass", "method", new CatchTransform());
    }

    @Test
    public void testPrepend() {
        applyPatch(
            "PrependClass",
            "method",
            new CatchTransform()
                .prepend("this.thrown = true;")
        );

        PrependClass instance = new PrependClass();
        instance.method();

        assertTrue("Prepended catch block should still throw", instance.thrown);
    }

    // Javassist doesn't currently support replacing the body of a Handler (catch/finally) block

    /*
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
    }*/

    // Mock classes
    class DummyClass {
        void method() {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PrependClass {
        boolean thrown = false;

        void method() {
            try {
                throw new Exception();
            } catch (Exception ignored) {}
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
