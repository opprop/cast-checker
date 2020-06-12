import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class WideningCheck {
	public int check(@IntRange(from=0, to=255) byte unsigned, byte signed) {
        //:: error: (assignment.type.incompatible)
        int value1 = unsigned;
        value1 = unsigned;
        
        int value2 = signed;    // OK
        value2 = signed;        // OK
        
        int value3 = unsigned & 0xFF;   // OK
        value3 = unsigned & 0xFF;       // OK
        int value4 = unsigned & 0xff;   // OK
        value4 = unsigned & 0xff;       // OK
        
        //:: error: (assignment.type.incompatible)
        int add = unsigned + 1;
        //:: error: (assignment.type.incompatible)
        add = unsigned + 1;
        add = (unsigned & 0xff) + 1; // OK
        
        int add2 = signed + 1;  // OK
        add2 = signed + 1;      // OK
        
        return unsigned;
    }
}