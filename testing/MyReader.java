package read;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.checkerframework.common.value.qual.IntRange;

public class MyReader {
    InputStream reader;

    MyReader(InputStream reader) {
        this.reader = reader;
    }

    public @IntRange(from=-1, to=255) int read() {
        int inbuff;
        try {
            inbuff = this.reader.read();
        } catch (IOException e) {
            inbuff = -1;
        }
        return inbuff;
    }
}