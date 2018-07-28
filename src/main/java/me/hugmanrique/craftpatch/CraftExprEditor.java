package me.hugmanrique.craftpatch;

import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import io.reflectoring.diffparser.api.model.Range;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.expr.*;

import java.util.*;

/**
 * @author Hugo Manrique
 * @since 28/07/2018
 */
public class CraftExprEditor extends ExprEditor {
    //private final Diff diff;
    private final CtClass clazz;

    //private final List<Line> lines;
    private Map<Integer, String> additions;
    private Set<Integer> removals;

    public CraftExprEditor(Diff diff, CtClass clazz) {
        this.clazz = clazz;
        this.additions = new HashMap<>();
        this.removals = new HashSet<>();

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

    private Line getLine(int lineNumber) {
    }

    @Override
    public void edit(NewExpr e) throws CannotCompileException {
        e.getLineNumber()


        super.edit(e);
    }

    @Override
    public void edit(NewArray a) throws CannotCompileException {
        super.edit(a);
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        super.edit(m);
    }

    @Override
    public void edit(ConstructorCall c) throws CannotCompileException {
        super.edit(c);
    }

    @Override
    public void edit(FieldAccess f) throws CannotCompileException {
        super.edit(f);
    }

    @Override
    public void edit(Instanceof i) throws CannotCompileException {
        super.edit(i);
    }

    @Override
    public void edit(Cast c) throws CannotCompileException {
        super.edit(c);
    }

    @Override
    public void edit(Handler h) throws CannotCompileException {
        super.edit(h);
    }
}
