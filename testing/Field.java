import org.checkerframework.common.value.qual.IntRange;

public class Field {
	byte byte_field;
	short short_field;
	char char_field;
	public void testField() {
		// :: error: (assignment.type.incompatible)
		@IntRange(from=0, to=255) byte value1 = byte_field;
		@IntRange(from=-128, to=127) byte value3 = byte_field;	// OK
		
		// :: error: (assignment.type.incompatible)
		@IntRange(from=0, to=65535) short value4 = short_field;
		@IntRange(from=-32768, to=32767) short value5 = short_field;	// OK
		
		@IntRange(from=0, to=65535) char value2 = char_field;	// OK
	}
}