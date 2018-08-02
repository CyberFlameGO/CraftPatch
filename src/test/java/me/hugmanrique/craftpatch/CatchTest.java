package me.hugmanrique.craftpatch;

import javassist.NotFoundException;
import me.hugmanrique.craftpatch.transform.expr.CatchTransform;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void testFilter() {
        applyPatch(
            "FilterClass",
            "method",
            new CatchTransform(handler -> {
                try {
                    String className = handler.getType().getSimpleName();
                    assertEquals("Exception type passed to filter is RuntimeException", "RuntimeException", className);

                    return false;
                } catch (NotFoundException e) {
                    e.printStackTrace();
                    fail();
                }

                return true;
            }).prepend("this.pass = false;")
        );

        FilterClass instance = new FilterClass();
        instance.method();

        assertTrue("Catch block hasn't been modified", instance.pass);
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

    class FilterClass {
        boolean pass = true;

        void method() {
            try {
                throw new RuntimeException();
            } catch (RuntimeException ignored) {}
        }
    }
}
