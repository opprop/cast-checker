import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;

public class Error {
	/**
     * Invokes the delegate's <code>read()</code> method.
     * @return the byte read or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     * @throws EOFException if an end of file is reached unexpectedly
     * commons-io/src/main/java/org/apache/commons/io/input/SwappedDataInputStream.java:73
     */
//    public byte readByte() throws IOException, EOFException
//    {
//    	InputStream in = new FileInputStream("afile");
//        return (byte)in.read();
//    }
    
    /**
     * Needed in readInstruction and subclasses in this package
     * @since 6.0
     * commons-bcel/src/main/java/org/apache/bcel/generic/Instruction.java:531
     */
//    final void setLength( final int length ) {
//        short length = (short) length; // TODO check range?
//    }

    /**
     * Set the local variable index.
     * also updates opcode and length
     * TODO Why?
     * @see #setIndexOnly(int)
     * commons-bcel/src/main/java/org/apache/bcel/generic/LocalVariableInstruction.java:173
     */
//    public void setIndex( final int n , short c_tag, int n) { // TODO could be package-protected?
//        if ((n < 0) || (n > Const.MAX_SHORT)) {
//            throw new ClassGenException("Illegal value: " + n);
//        }
//        // Cannot be < 0 as this is checked above
//        if (n <= 3) { // Use more compact instruction xLOAD_n
//            short value = (short) (c_tag + n);
//        }
//    }
    
    /**
     * commons-bcel/src/main/java/org/apache/bcel/classfile/Signature.java:185
     */
//    private static void matchIdent( final InputStream in, final StringBuilder buf ) {
//        int ch = in.read();
//        do {
//            buf.append((char) ch);
//            ch = in.read();
//        } while (ch != -1);
//    }
    
    /**
     * commons-imaging/src/main/java/org/apache/commons/imaging/formats/psd/datareaders/UncompressedDataReader.java:61
     */
//    public void readData(final InputStream is, final int[][]][] data) throws ImageReadException, IOException {
//	    final int b = in.read();
//	    data[0][0][0] = (byte) b;
//    }
}
