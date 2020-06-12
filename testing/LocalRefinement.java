import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class LocalRefinement {
    public void assignRefinementCheck() throws IOException {
        InputStream in = new FileInputStream("afile");
        int inbuff = 0;
        @SuppressWarnings("unused")
        byte data;

        if ((inbuff = in.read()) != -1) {
            data = (byte) inbuff;    // OK
            @IntRange(from=0, to=255) byte value = data; //OK
        }

        in.close();
    }

    public @IntRange(from=0, to=255) byte returnUnsighByte() { return 0; }
    public byte[] returnByteArray() { return null; }
    public byte returnSignedByte() { return 0; }

    public void returnRefinementCheck() {
        @IntRange(from=0, to=255) byte data1 = (byte) returnUnsighByte();    //OK
        //:: error: (assignment.type.incompatible)
        @IntRange(from=0, to=255) byte data2 = (byte) returnUnsighByteError();
        @IntRange(from=-128, to=127) byte data3 = returnByteArray()[0];  //OK
    }
}