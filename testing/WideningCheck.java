import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class WideningCheck {
	public int check(@IntRange(from=0, to=255) byte unsigned, @IntRange(from=-128, to=127) byte signed) {
        // :: warning: (assignment.type.incompatible)
        int value1 = unsigned;
        // :: warning: (assignment.type.incompatible)
        value1 = unsigned;

        int value2 = signed;    // OK
        value2 = signed;        // OK

        int value3 = unsigned & 0xFF;   // OK
        value3 = 0xFF & unsigned;       // OK
        int value4 = unsigned & 0xff;   // OK
        value4 = 0xff & unsigned;       // OK

        // :: warning: (assignment.type.incompatible)
        int add = unsigned + 1;
        // :: warning: (assignment.type.incompatible)
        add = 1 + unsigned;
        add = (unsigned & 0xff) + 1; // OK

        int add2 = signed + 1;  // OK
        add2 = 1 + signed;      // OK
        
        int add3 = unsigned + signed;
        add3 = unsigned + unsigned;
        add3 = signed + signed;
        
        int add4 = (byte) 200 + 1;
        add4 = (byte) 200 + (byte) 200;
        add4 = 1 + 1;
        
        byte add5 = (byte) (unsigned + unsigned);

        // :: warning: (return.type.incompatible)
        return unsigned;
    }
}