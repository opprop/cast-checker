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
    	Range signedIntersect = range.intersect(signedByteRange());
    	Range UnSignedIntersect= range.intersect(unsignedByteRange());

    	if (UnSignedIntersect.isNothing()) {
    		return false;
    	} else if (signedIntersect.isNothing()) {
    		return true;
    	} else {
    		return (UnSignedIntersect.to - UnSignedIntersect.from)
    				> (signedIntersect.to - signedIntersect.from);
    	}
    }

    /** Return true if this range contains unsigned part of {@code short} value. */
    public static boolean isUnsignedShort(Range range) {
    	Range signedIntersect = range.intersect(signedShortRange());
    	Range UnSignedIntersect= range.intersect(unsignedShortRange());

    	if (UnSignedIntersect.isNothing()) {
    		return false;
    	} else if (signedIntersect.isNothing()) {
    		return true;
    	} else {
    		return (UnSignedIntersect.to - UnSignedIntersect.from)
    				> (signedIntersect.to - signedIntersect.from);
    	}
    }
}
