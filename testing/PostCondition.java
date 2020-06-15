import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.checkerframework.common.value.qual.IntRange;
import cast.qual.EnsuresIntRangeIf;

public class PostCondition {
	/**
	 * commons-csv/src/main/java/org/apache/commons/csv/Lexer.java:264
	 */
	private void parseEncapsulatedToken() throws IOException {
		int c;
		Reader reader = new FileReader("afile");
		c = reader.read();
		if (!isEndOfFile(c)) {
			char val = (char) c;	// OK
		}
	}

	/**
	 * @return true if the given character indicates end of file
	 */
	@EnsuresIntRangeIf(expression="#1", result=false, from=0, to=65535)
	boolean isEndOfFile(final @IntRange(from=-1, to=65535) int ch) {
		return ch == -1;
	}

}