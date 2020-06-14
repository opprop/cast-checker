import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class MethodInvocation {
    public byte returnSignedByte() {
    	return 0;
    }

    public @IntRange(from=0, to=255) byte returnUnsignedByte() {
        return 0;
    }
    
    public void test() {
    	// :: error: (assignment:type:incompatile)
    	@IntRange(from=0, to=255) byte unsigned1 = returnSignedByte();
    	@IntRange(from=-128, to=127) byte signed1 = returnSignedByte();
    	
    	@IntRange(from=0, to=255) byte unsigned2 = returnUnsignedByte();
    	// :: error: (assignment:type:incompatile)
    	@IntRange(from=-128, to=127) byte signed2 = returnUnsignedByte();
    }
}