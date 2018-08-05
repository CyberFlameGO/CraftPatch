package me.hugmanrique.craftpatch.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Hugo Manrique
 * @since 05/08/2018
 */
public class StreamUtil {
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
        copyStream(in, out);

        return out.toByteArray();
    }

    public static void copyStream(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[8192];

        while (true) {
            int read = from.read(buf);

            if (read == -1) {
                return;
            }

            to.write(buf, 0, read);
        }
    }

    public static byte[] getResource(Class<?> clazz) throws IOException {
        return toByteArray(
            clazz.getClassLoader().getResourceAsStream(
                ClassUtil.getClassResourceName(clazz)
            )
        );
    }
}
