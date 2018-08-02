package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.expr.NewArrayTransform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class NewArrayTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("DummyClass", "method", new NewArrayTransform());
    }

    @Test
    public void testDimensionChange() {
        applyPatch(
            "DimensionChange",
            "method",
            new NewArrayTransform()
                .setDimensions(2, 2)
        );

        DimensionChange instance = new DimensionChange();
        int[][] array = instance.method();

        assertEquals("Array dimensions must have changed", 2, array.length);
        assertEquals("Subarray dimensions must have changed", 2, array[0].length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoDimensions() {
        applyPatch(
            "NoDimensions",
            "method",
            new NewArrayTransform()
                .setDimensions()
        );
    }

    // Mock classes
    class DummyClass {
        void method() {
            int[] array = new int[3];
        }
    }

    class DimensionChange {
        int[][] method() {
            return new int[3][5];
        }
    }

    class NoDimensions {
        void method() {
            int[] array = new int[4];
        }
    }
}
