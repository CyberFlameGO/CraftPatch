package me.hugmanrique.craftpatch;

import javassist.NotFoundException;
import me.hugmanrique.craftpatch.patch.simple.SimplePatch;
import me.hugmanrique.craftpatch.transform.expr.CastTransform;
import me.hugmanrique.craftpatch.transform.expr.FieldAccessTransform;

/**
 * @author Hugo Manrique
 * @since 05/08/2018
 */
public class PlayGround {
    private void main() {
        PatchApplier applier = new PatchApplier();

        Patch patch = new SimplePatch("com.mypackage.MyClass", "");

        FieldAccessTransform transform =
                new FieldAccessTransform(fieldAccess -> fieldAccess.getFieldName().equals("ownText"))
                        .setResult("\"def\"");

        Transformation castTransform =
                new CastTransform(cast -> {
                    try {
                        return cast.getType().getName().equals("List");
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .setCastClass("ArrayList")
                .append("if (!($1 instanceof java.util.ArrayList)) { $1 = new java.util.ArrayList($1); }");

        try {
            applier.applyPatch(patch, true);
        } catch (PatchApplyException e) {
            e.printStackTrace();
        }
    }
}
