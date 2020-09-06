package cast;

import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.framework.qual.StubFiles;

@StubFiles({
    "input.astub",
    "output.astub",
    "JavaUtil.astub",
    "JavaLang.astub",
    "JavaIO.astub",
    "JavaMath.astub",
    "JavaNIO.astub",
    "JavaBoxedPrimitives.astub",
})
public class CastChecker extends ValueChecker {

    @Override
    protected BaseTypeVisitor<?> createSourceVisitor() {
        return new CastVisitor(this);
    }
}
