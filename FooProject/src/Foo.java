import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class Foo {
    public void testDigitExcute(String orig) throws IOException {
        char octal_char = 0;
        int this_esc = orig.indexOf('\\');
        int ii = this_esc + 1;
        while (ii < orig.length()) {
            char ch = orig.charAt(ii++);
            if ((ch < '0') || (ch > '8')) {
             break;
            }
            char octal_char_cast = (char) ((octal_char * 8) + Character.digit(ch, 8));
        }
    }
	
	public static long readSwappedUnsignedInteger(final @IntRange(from=0, to=255) byte[] data, int offset) {
        final long low = ( ( ( data[ offset + 0 ] & 0xff ) << 0 ) +
                     ( ( data[ offset + 1 ] & 0xff ) << 8 ) +
                     ( ( data[ offset + 2 ] & 0xff ) << 16 ) );

        final long high = data[ offset + 3 ] & 0xff;
        
        return (high << 24) + (0xffffffffL & low); 
    }
}