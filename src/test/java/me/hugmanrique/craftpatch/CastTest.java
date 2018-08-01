package me.hugmanrique.craftpatch;

import javassist.NotFoundException;
import me.hugmanrique.craftpatch.transform.CastTransform;
import org.junit.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

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

    @Test
    public void testFilter() {
        applyPatch(
            "FilterClass",
            "method",
            new CastTransform(cast -> {
                try {
                    String className = cast.getType().getSimpleName();
                    assertEquals("Type passed to filter is ArrayList", "ArrayList", className);

                    return true;
                } catch (NotFoundException e) {
                    e.printStackTrace();
                    fail();
                }

                return false;
            }).prepend("$1 = new java.util.ArrayList();")
        );

        FilterClass instance = new FilterClass();
        instance.method();
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

    class FilterClass {
        List list = new Vector();

        ArrayList method() {
            // Will throw ClassCastException
            return (ArrayList) list;
        }
    }
}
