import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class WideningCheck {
	public int checkByte(@IntRange(from=0, to=255) byte unsigned, @IntRange(from=-128, to=127) byte signed) {
        // :: warning: (widening.unsafe)
        int value1 = unsigned;
        // :: warning: (widening.unsafe)
        value1 = unsigned;

        int value2 = signed;    // OK
        value2 = signed;        // OK

        int value3 = unsigned & 0xFF;   // OK
        value3 = 0xFF & unsigned;       // OK
        int value4 = unsigned & 0xff;   // OK
        value4 = 0xff & unsigned;       // OK

        // :: warning: (widening.unsafe)
        int add = unsigned + 1;
        // :: warning: (widening.unsafe)
        add = 1 + unsigned;
        add = (unsigned & 0xff) + 1; // OK

        int add2 = signed + 1;  // OK
        add2 = 1 + signed;      // OK
        
        // :: warning: (binary.signedness.mismatch)
        int add3 = unsigned + signed;
        // :: warning: (widening.unsafe)
        add3 = unsigned + unsigned;
        add3 = signed + signed;
        
        // :: warning: (widening.unsafe)
        int add4 = (byte) 200 + 1;

        // :: warning: (widening.unsafe)
        return unsigned;
    }
	
	public int checkShort(@IntRange(from=0, to=65535) short unsigned, @IntRange(from=-32768, to=32767) short signed) {
        // :: warning: (widening.unsafe)
        int value1 = unsigned;
        // :: warning: (widening.unsafe)
        value1 = unsigned;

        int value2 = signed;    // OK
        value2 = signed;        // OK

        int value3 = unsigned & 0xFFFF;   // OK
        value3 = 0xFFFF & unsigned;       // OK
        int value4 = unsigned & 0xffff;   // OK
        value4 = 0xffff & unsigned;       // OK

        // :: warning: (widening.unsafe)
        int add = unsigned + 1;
        // :: warning: (widening.unsafe)
        add = 1 + unsigned;
        add = (unsigned & 0xffff) + 1; // OK

        int add2 = signed + 1;  // OK
        add2 = 1 + signed;      // OK
        
        // :: warning: (binary.signedness.mismatch)
        int add3 = unsigned + signed;
        // :: warning: (widening.unsafe)
        add3 = unsigned + unsigned;
        add3 = signed + signed;
        
        // :: warning: (widening.unsafe)
        int add4 = (byte) 200 + 1;

        // :: warning: (widening.unsafe)
        return unsigned;
    }
}