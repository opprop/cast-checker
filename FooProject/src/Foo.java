import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class Foo {
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

    public byte[] returnByteArray() { return null; }
    public byte returnSignedByte() { return 0; }

    public void returnRefinementCheck() {
        @IntRange(from=0, to=255) byte data1 = (byte) returnUnsighByte();    //OK
        //:: error: (assignment.type.incompatible)
        @IntRange(from=0, to=255) byte data2 = (byte) returnUnsighByteError();
        
        @IntRange(from=-128, to=127) byte data3 = returnByteArray()[0];  //OK

    }

    public void testDigitExcute(String orig) throws IOException {
        char octal_char = 0;
        int this_esc = orig.indexOf('\\');
        int ii = this_esc + 1;
        while (ii < orig.length()) {
            char ch = orig.charAt(ii++);
            if ((ch < '0') || (ch > '8')) {
             break;
            }
            char octal_char_cast = (char) ((octal_char * 8) + Character.digit(ch, 8));
        }
    }
    
	byte[] byte_field_array;
	char[] char_field_array;
	byte[][] byte_double_array;
	char[][] char_double_array;
	public void testArray(byte[] byte_array, char[] char_array) {
		//:: error: (assignment.type.incompatible)
		@IntRange(from=0, to=255) byte value = byte_array[0];
		
		//:: error: (assignment.type.incompatible)
		@IntRange(from=0, to=255) byte value1 = byte_field_array[0];
		
		@IntRange(from=-128, to=127) byte value2 = byte_array[0];			// OK
		@IntRange(from=-128, to=127) byte value3 = byte_field_array[0];		// OK
		@IntRange(from=0, to=65535) char value4 = char_array[0];			// OK
		@IntRange(from=0, to=65535) char value5 = char_field_array[0];		// OK
		
		//:: error: (assignment.type.incompatible)
		@IntRange(from=0, to=255) byte value6 = byte_double_array[0][0];
		
		@IntRange(from=-128, to=127) byte value7 = byte_double_array[0][0];	// OK
		@IntRange(from=0, to=65535) char value8 = char_double_array[0][0];	// OK
		
	}
	
	public static long readSwappedUnsignedInteger(final @IntRange(from=0, to=255) byte[] data, int offset) {
        final long low = ( ( ( data[ offset + 0 ] & 0xff ) << 0 ) +
                     ( ( data[ offset + 1 ] & 0xff ) << 8 ) +
                     ( ( data[ offset + 2 ] & 0xff ) << 16 ) );

        final long high = data[ offset + 3 ] & 0xff;
        
        return (high << 24) + (0xffffffffL & low); 
    }
	
	private static void toBytes(final long bits, final @IntRange(from=0, to=255) byte[] result) {
		result[0] = (byte) (0xff & bits);			// OK
		result[1] = (byte) (bits & 0xff);			// OK
        result[2] = (byte) ((bits >> 0) & 0xff);	// OK
        result[3] = (byte) (0xff & (bits >> 8));	// OK
    }
}