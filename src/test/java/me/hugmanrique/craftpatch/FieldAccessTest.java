package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.FieldAccessTransform;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class FieldAccessTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("PlainClass", "getNumber", new FieldAccessTransform());
    }

    @Test
    public void testOverride() {
        applyPatch(
            "OverrideClass",
            "getText",
            new FieldAccessTransform()
                .setResult("\"def\"")
        );

        OverrideClass instance = new OverrideClass();
        assertEquals("Method must return overridden result", "def", instance.getText());
    }

    @Test
    public void testPrepend() {
        applyPatch(
            "PrependClass",
            "getNumbers",
            new FieldAccessTransform()
                .prepend("this.other = true;")
        );

        PrependClass instance = new PrependClass();
        instance.getNumbers();

        assertTrue("Prepend statement must set field to true", instance.other);
    }

    public static void callMethod(String value) {
        if (!"pass".equals(value)) {
            fail();
        }
    }

    @Test
    public void testMethodAppend() {
        Method method = getMethod("callMethod", String.class);

        applyPatch(
            "MethodAppend",
            "getText",
            new FieldAccessTransform()
                .callAfter(method, "\"pass\"")
        );

        MethodAppend instance = new MethodAppend();
        instance.getText();
    }

    @Test
    public void testFilter() {
        applyPatch(
            "FilterClass",
            "getPi",
            new FieldAccessTransform(fieldAccess -> {
                assertEquals("pi", fieldAccess.getFieldName());
                return false;
            }).setResult("3.14D")
        );

        FilterClass instance = new FilterClass();
        assertEquals("Non-transformed field access must return original value", Math.PI, instance.getPi(), DELTA);
    }

    // Mock classes
    class PlainClass {
        int number = 123;

        int getNumber() {
            return number;
        }
    }

    class OverrideClass {
        String text = "abc";

        String getText() {
            return text;
        }
    }

    class PrependClass {
        int[] numbers = { 1, 2, 3 };
        boolean other = false;

        int[] getNumbers() {
            return numbers;
        }
    }

    class MethodAppend {
        String text = "abc";

        String getText() {
            return text;
        }
    }

    class FilterClass {
        double pi = Math.PI;

        double getPi() {
            return pi;
        }
    }

}
