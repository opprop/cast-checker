package cast;

import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.framework.qual.StubFiles;

@StubFiles({
    "jdk.astub",
    "output.astub",
})
public class CastChecker extends ValueChecker {

    @Override
    protected BaseTypeVisitor<?> createSourceVisitor() {
        return new CastVisitor(this);
    }
}
