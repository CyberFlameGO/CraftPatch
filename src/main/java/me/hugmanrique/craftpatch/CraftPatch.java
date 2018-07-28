package me.hugmanrique.craftpatch;

import io.reflectoring.diffparser.api.DiffParser;
import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Diff;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Manrique
 * @since 28/07/2018
 */
public class CraftPatch {
    private final DiffParser parser;

    public CraftPatch() {
        this.parser = new UnifiedDiffParser();
    }

    public void applyPatch(File patchFile) throws IOException {
        List<Diff> diffs = parser.parse(patchFile);
    }

    public void applyPatch(byte[] patchBytes) {
        List<Diff> diffs = parser.parse(patchBytes);
    }

    public void applyPatch(InputStream in) {
        List<Diff> diffs = parser.parse(in);

    }

    private void applyDiff(Diff diff) {
        if (!Objects.equals(diff.getFromFileName(), diff.getToFileName())) {
            // We cannot refactor file renames
            return;
        }

        ClassPool pool = ClassPool.getDefault();

        try {
            CtClass clazz = pool.getCtClass(diff.getToFileName());

            clazz.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) throws CannotCompileException {
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
            });


        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
