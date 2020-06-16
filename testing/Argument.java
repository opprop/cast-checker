

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class Argument {
    public byte returnSignedByte() { return 0; }
    public byte[] returnByteArray() { return null; }
    public @IntRange(from=0, to=255) byte returnUnsighByte() { return 0; }
    public void acceptByteArray(byte[] value) {}
    public void acceptSignedByte(byte value) {}
    public void acceptUnsignedByte(@IntRange(from=0, to=255) byte value) {}
    public void acceptSignedByteArray(byte[] value) {}
    public void acceptUnsignedByteArray(@IntRange(from=0, to=255) byte[] value) {}
    public void acceptChar(char value) {}
    public void acceptCharArray(char[] value) {}
    public void acceptSignedByteDoubleArray(byte[][] value) {}
    public void acceptUnsignedByteDoubleArray(@IntRange(from=0, to=255) byte[][] value) {}
    public void acceptCharDoubleArray(char[][] value) {}

    public void testPassingArgument(@IntRange(from=0, to=255) int value, 
                                    byte byte_val,
                                    char char_val, 
                                    byte[] byte_arr, 
                                    char[] char_arr, 
                                    byte[][] byte_double_arr, 
                                    char[][] char_double_arr) {
        byte data = (byte) value;
        //:: error: (argument.type.incompatible)
        acceptSignedByte(data);]
        acceptUnsignedByte(data);   // OK

        byte signed = returnSignedByte();
        byte[] data_array = {signed};
        acceptByteArray(data_array);    // OK

        byte unsigned = returnUnsighByte();
        byte[] unsigned_array = {unsigned};
        //:: error: (argument.type.incompatible)
        acceptByteArray(unsigned_array);
        acceptByteArray(returnByteArray());    	// OK

        //:: error: (argument.type.incompatible)
        acceptUnsignedByte(byte_val);
        acceptSignedByte(byte_val);   // OK
        acceptChar(char_val);   // OK

        //:: error: (argument.type.incompatible)
        acceptUnsignedByteArray(byte_arr);
        acceptSignedByteArray(byte_arr);	// OK
        acceptCharArray(char_arr);	// OK

        //:: error: (argument.type.incompatible)
        acceptUnsignedByteDoubleArray(byte_double_arr);
        acceptSignedByteDoubleArray(byte_double_arr);	// OK
        acceptCharDoubleArray(char_double_arr);	// OK
    }
}