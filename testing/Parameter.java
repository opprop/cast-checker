import org.checkerframework.common.value.qual.IntRange;

public class Parameter {
    public void testParameter(byte value, short value1, char value2) {
        //:: error: (assignment.type.incompatible)
        @IntRange(from=0, to=255) byte data1 = value;
        @IntRange(from=-128, to=127) byte data2 = value;    // OK
        
        //:: error: (assignment.type.incompatible)
        @IntRange(from=0, to=65535) short data4 = value1;
        @IntRange(from=-32768, to=32767) short data5 = value1;    // OK
        
        @IntRange(from=0, to=65535) char data3 = value2;   //OK
    }
}