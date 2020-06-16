package cast;

import org.checkerframework.common.value.util.Range;

public class CastRangeUtil {
    /** A range containing all possible signed byte values. */
    public static Range signedByteRange() {
        return Range.create(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    /** A range containing all possible signed short values. */
    public static Range signedShortRange() {
        return Range.create(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    /** A range containing all possible unsigned byte values. */
    public static Range unsignedByteRange() {
        return Range.create(0, Byte.MAX_VALUE * 2 + 1);
    }

    /** A range containing all possible unsigned short values. */
    public static Range unsignedShortRange() {
        return Range.create(0, Short.MAX_VALUE * 2 + 1);
    }

    /** Return true if this range contains signed part of {@code byte} value. */
    public static boolean isSignedByte(Range range) {
        return !isUnsignedByte(range);
    }

    /** Return true if this range contains signed part of {@code short} value. */
    public static boolean isSignedShort(Range range) {
        return !isUnsignedShort(range);
    }

    /** Return true if this range contains unsigned part of {@code byte} value. */
    public static boolean isUnsignedByte(Range range) {
        return !range.intersect(Range.create(Byte.MAX_VALUE + 1, Byte.MAX_VALUE * 2 + 1))
                        .isNothing()
                && !range.contains(signedByteRange());
    }

    /** Return true if this range contains unsigned part of {@code short} value. */
    public static boolean isUnsignedShort(Range range) {
        return !range.intersect(Range.create(Short.MAX_VALUE + 1, Short.MAX_VALUE * 2 + 1))
                        .isNothing()
                && !range.contains(signedShortRange());
    }
}
