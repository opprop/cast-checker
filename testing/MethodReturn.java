import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class MethodReturn {
    public byte returnUnsighByteError() throws IOException {
        InputStream in = new FileInputStream("afile");
        @SuppressWarnings("unused")
        int data;
        while ((data = in.read()) != -1) {
            //:: error: (return.type.incompatible)
            return (byte) data;
        }

        return (byte) 0;
    }

    public @IntRange(from=0, to=255) byte returnUnsighByte() throws IOException {
        InputStream in = new FileInputStream("afile");
        @SuppressWarnings("unused")
        int data;
        while ((data = in.read()) != -1) {
            return (byte) data;    // OK
        }

        return (byte) 0;
    }
}