package cast;

import org.checkerframework.common.value.qual.IntRange;

public class SignednessConvert {
    public static byte toSignedByte(@IntRange(from=0, to=255) int val) {
        return (byte) val;
    }
    
    public static byte unsignedToSignedByte(@IntRange(from=0, to=255) byte val) {
        return val;
    }
    
    public static @IntRange(from=0, to=255) byte signedToUnsignedByte(byte val) {
        return val;
    }
    
    public static short toSignedShort(@IntRange(from=0, to=65535) int val) {
        return (short) val;
    }
    
    public static char signedShortToChar(short val) {
        return (char) val;
    }
    
    public static byte[] toSignedByteArray(@IntRange(from=0, to=255) byte[] val) {
        return val;
    }
    
}
