package me.hugmanrique.craftpatch;

import me.hugmanrique.craftpatch.patch.simple.SimplePatch;
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

        try {
            applier.applyPatch(patch, true);
        } catch (PatchApplyException e) {
            e.printStackTrace();
        }
    }
}
