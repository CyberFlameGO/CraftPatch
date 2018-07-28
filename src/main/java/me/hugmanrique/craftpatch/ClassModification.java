package me.hugmanrique.craftpatch;

import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.reflectoring.diffparser.api.model.Range;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Hugo Manrique
 * @since 28/07/2018
 */
@Getter
public class ClassModification {
    private Map<Integer, String> additions = new HashMap<>();
    private Set<Integer> removals = new HashSet<>();

    public ClassModification(Diff diff) {
        for (Hunk hunk : diff.getHunks()) {
            Range range = hunk.getFromFileRange();
            int hunkStart = range.getLineStart();

            int lineCount = 0;

            for (Line line : hunk.getLines()) {
                switch (line.getLineType()) {
                    case FROM:
                        removals.add(hunkStart + lineCount);
                        break;
                    case TO:
                        additions.put(hunkStart + lineCount, line.getContent());
                        break;
                }

                lineCount++;
            }
        }
    }
}
