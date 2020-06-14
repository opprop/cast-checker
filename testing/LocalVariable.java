import org.checkerframework.common.value.qual.IntRange;

public class LocalVariable {
    public void test(byte signed, @IntRange(from=0, to=255) byte unsigned) {
        byte data = signed;
        //:: error: (assignment.type.incompatible)
        @IntRange(from=0, to=255) byte data1 = data;
        @IntRange(from=-128, to=127) byte data2 = data;		// OK
        
        data = unsigned;
        @IntRange(from=0, to=255) byte data3 = data;		// OK
        //:: error: (assignment.type.incompatible)
        @IntRange(from=-128, to=127) byte data4 = data;
    }
}