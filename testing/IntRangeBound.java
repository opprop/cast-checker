public class IntRangeBound {
	public void intValBoundTest() {
    	byte value1 = (byte) 255;	// OK
    	short value2 = (short) 65535;	// OK
    	char value3 = (char) 65535;	// OK
    	
    	//:: error: (cast.unsafe)
    	byte value4 = (byte) 256;
    	//:: error: (cast.unsafe)
    	short value5 = (short) 65536;
    	//:: error: (cast.unsafe)
    	char value6 = (char) 65536;
    	
    	byte value7 = (byte) -128;	// OK
    	short value8 = (short) -32768;	// OK
    	char value9 = (char) 0;	// OK
    	
    	//:: error: (cast.unsafe)
    	byte value10 = (byte) -129;
    	//:: error: (cast.unsafe)
    	short value11 = (short) -32769;
    	//:: error: (cast.unsafe)
    	char value12 = (char) -1;
    }
}