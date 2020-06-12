package cast;

import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.value.ValueTransfer;
import org.checkerframework.common.value.qual.UnknownVal;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.javacutil.AnnotationBuilder;

public class CastTransfer extends ValueTransfer {

    protected final CastAnnotatedTypeFactory atypefactory;
    protected final AnnotationMirror UNKNOWNVAL;

    public CastTransfer(CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        super(analysis);
        atypefactory = (CastAnnotatedTypeFactory) analysis.getTypeFactory();
        UNKNOWNVAL =
                AnnotationBuilder.fromClass(
                        analysis.getTypeFactory().getElementUtils(), UnknownVal.class);
    }
}
