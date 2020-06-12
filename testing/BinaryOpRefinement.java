import java.io.Reader;
import java.io.IOException;

public class BinaryOpRefinement {
    void testLessThan(Reader in) throws IOException {
        char cur;
        int buff;
        do {
            buff = in.read();
            if (buff < 0) {
                break;
            }
            cur = (char) buff; // OK
        } while (true);

        do {
            buff = in.read();
            if (buff < 0) {
                ;
            }
            // :: error: (cast.unsafe)
            cur = (char) buff;
        } while (true);
    }

    void testGreaterThanOrEqual(Reader in) throws IOException {
        char cur;
        int buff;
        while ((buff = in.read()) >= 0) {
            cur = (char) buff; // OK
        }

        while ((buff = in.read()) > 0) {
            cur = (char) buff;
        }
    }

    void testEqualTo(Reader in) throws IOException {
        char cur;
        int buff;
        while ((buff = in.read()) != -1) {
            cur = (char) buff; // OK
        }

        do {
            buff = in.read();
            if (buff == -1) {
                break;
            }
            cur = (char) buff; // OK
        } while (true);

        do {
            buff = in.read();
            if (buff == -1) {
                ;
            }
            // :: error: (cast.unsafe)
            cur = (char) buff;
        } while (true);
    }
}
