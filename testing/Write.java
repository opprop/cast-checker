import java.io.IOException;
import java.io.OutputStream;

public class Write {
	public static void writeSwappedInteger(final OutputStream output, final int value)
	        throws IOException
    {
		// :: warning: (widening.unsafe)
        output.write( (byte)( ( value >> 0 ) & 0xff ) );
        // :: warning: (widening.unsafe)
        output.write( (byte)( ( value >> 8 ) & 0xff ) );
        // :: warning: (widening.unsafe)
        output.write( (byte)( ( value >> 16 ) & 0xff ) );
        // :: warning: (widening.unsafe)
        output.write( (byte)( ( value >> 24 ) & 0xff ) );
    }
	
	public static void correct(final OutputStream output, final int value)
	        throws IOException
    {
        output.write( ( value >> 0 ) & 0xff );
        output.write( ( value >> 8 ) & 0xff );
        output.write( ( value >> 16 ) & 0xff );
        output.write( ( value >> 24 ) & 0xff );
    }
}