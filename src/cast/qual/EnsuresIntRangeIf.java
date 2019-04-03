package cast.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.framework.qual.ConditionalPostconditionAnnotation;
import org.checkerframework.framework.qual.InheritedAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

/**
 * Indicates that the given expressions are within the specified integral range, if the method 
 * returns the given result (either true or false).
 *
 * <p>Here are ways this conditional postcondition annotation can be used:
 *
 * <p><b>Method parameters:</b> A common example is that the {@code equals} method is annotated as
 * follows:
 *
 * <pre>{@code   @EnsuresIntRangeIf(expression="#1", result=true, from=0, to=255)
 *   public boolean equals(@IntRange(from=0, to=255) Object obj) { ... }}</pre>
 *
 * because, if {@code equals} returns true, then the first (#1) argument to {@code equals} is within 
 * specified range.
 *
 * <p><b>Fields:</b> The value expressions can refer to fields, even private ones. For example:
 *
 * <pre>{@code   @EnsuresIntRangeIf(expression="this.derived", result=true, from=0, to=255)
 *   public boolean isDerived() {
 *     return (this.derived != -1);
 *   }}</pre>
 *
 * As another example, an {@code Iterator} may cache the next value that will be returned, in which
 * case its {@code hasNext} method could be annotated as:
 *
 * <pre>{@code   @EnsuresIntRangeIf(expression="next_cache", result=true, from=0, to=255)
 *   public boolean hasNext() {
 *     if (next_cache == -1) return false;
 *     ...
 *   }}</pre>
 *
 * An {@code @EnsuresIntRangeIf} annotation that refers to a private field is useful for verifying
 * that client code performs needed checks in the right order, even if the client code cannot
 * directly affect the field.
 *
 * You can write multiple {@code @EnsuresIntRangeIf} annotations on a single method:
 *
 * <pre><code>
 * &nbsp;   @EnsuresIntRangeIf(expression="outputFile", result=true, from=0, to=255)
 * &nbsp;   @EnsuresIntRangeIf(expression="memoryOutputStream", result=false, from=0, to=255)
 *     public boolean isThresholdExceeded() { ... }
 * </code></pre>
 *
 * @see IntRange
 * @see org.checkerframework.checker.value.ValueChecker
 * @checker_framework.manual #constant-value-checker Constant Value Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@ConditionalPostconditionAnnotation(qualifier = IntRange.class)
@InheritedAnnotation
public @interface EnsuresIntRangeIf {
    /**
     * Java expression(s) that are non-null after the method returns the given result.
     *
     * @checker_framework.manual #java-expressions-as-arguments Syntax of Java expressions
     */
    String[] expression();

    /** The return value of the method that needs to hold for the postcondition to hold. */
    boolean result();
    
    /** Smallest value in the range, inclusive. */
    @QualifierArgument("from")
    long from() default Long.MIN_VALUE;
    
    /** Largest value in the range, inclusive. */
    @QualifierArgument("to")
    long to() default Long.MAX_VALUE;
}
