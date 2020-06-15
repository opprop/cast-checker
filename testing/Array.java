import java.io.IOException;
import java.io.OutputStream;

import org.checkerframework.common.value.qual.IntRange;

public class Array {
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
	
	/**
	 * commons-io/src/main/java/org/apache/commons/io/HexDump.java:104
	 */
	public static void dump(final byte[] data, final long offset, final OutputStream stream, final int index)
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		final StringBuilder buffer = new StringBuilder(74);
		int chars_read = data.length;
		for (int j = 0; j < chars_read; j++) {
			for (int k = 0; k < chars_read; k++) {
				if (data[k + j] >= ' ' && data[k + j] < 127) {
					buffer.append((char) data[k + j]);			// OK
				} else {
					buffer.append('.');
				}
			}
		}
	}
}