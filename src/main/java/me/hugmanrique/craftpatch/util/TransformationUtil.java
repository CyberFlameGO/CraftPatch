package me.hugmanrique.craftpatch.util;

import me.hugmanrique.craftpatch.Transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Hugo Manrique
 * @since 02/08/2018
 */
public class TransformationUtil {
    public static List<Transformation> getTransformations(Collection<Transformation> transformations, Class<?> type) {
        List<Transformation> filtered = new ArrayList<>();

        for (Transformation transformation : transformations) {
            if (transformation.getType().equals(type)) {
                filtered.add(transformation);
            }
        }

        return filtered;
    }
}
