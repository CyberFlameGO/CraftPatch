import java.io.File;

/**
 * @author Hugo Manrique
 * @since 28/07/2018
 */
public class PatchParseTest {
    private File getPatchFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("testpatch.patch").getFile());
    }
}
