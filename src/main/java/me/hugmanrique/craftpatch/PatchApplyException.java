package me.hugmanrique.craftpatch;

/**
 * @author Hugo Manrique
 * @since 05/08/2018
 */
public class PatchApplyException extends Exception {
    public PatchApplyException() {
    }

    public PatchApplyException(String message) {
        super(message);
    }

    public PatchApplyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PatchApplyException(Throwable cause) {
        super(cause);
    }
}
