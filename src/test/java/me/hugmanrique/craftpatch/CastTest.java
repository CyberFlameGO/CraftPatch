package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.transform.CastTransform;
import org.junit.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

/**
 * @author Hugo Manrique
 * @since 01/08/2018
 */
public class CastTest extends PatchTest {
    @Test(expected = NullPointerException.class)
    public void testNullStatement() {
        applyPatch("DummyClass", "method", new CastTransform());
    }

    @Test
    public void testClassReplacement() {
        applyPatch(
            "ReplacementClass",
            "method",
            new CastTransform()
                .setCastClass(ArrayList.class)
        );

        ReplacementClass instance = new ReplacementClass();
        instance.method();
    }

    @Test
    public void testObjectReplacement() {
        applyPatch(
            "ObjectReplaceClass",
            "method",
            new CastTransform()
                .setObject("new java.util.Vector()")
        );

        ObjectReplaceClass instance = new ObjectReplaceClass();
        assertTrue("Replaced object must be instance of Vector", instance.method() instanceof Vector);
    }

    // Mock classes
    class DummyClass {
        List<String> list = new ArrayList<>();

        ArrayList<String> method() {
            return (ArrayList<String>) list;
        }
    }

    class ReplacementClass {
        List<String> list = new ArrayList<>();

        AbstractList<String> method() {
            return (AbstractList<String>) list;
        }
    }

    class ObjectReplaceClass {
        List<String> list = new ArrayList<>();

        AbstractList<String> method() {
            return (AbstractList<String>) list;
        }
    }
}
