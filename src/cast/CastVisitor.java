package cast;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueVisitor;
import org.checkerframework.common.value.qual.IntVal;
import org.checkerframework.common.value.qual.UnknownVal;
import org.checkerframework.common.value.util.Range;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class CastVisitor extends ValueVisitor {

    /** The top type for this hierarchy. */
    protected final AnnotationMirror UNKNOWNVAL;

    public CastVisitor(BaseTypeChecker checker) {
        super(checker);
        UNKNOWNVAL = AnnotationBuilder.fromClass(elements, UnknownVal.class);
    }

    @Override
    protected CastAnnotatedTypeFactory createTypeFactory() {
        return new CastAnnotatedTypeFactory(checker);
    }

    @Override
    protected void commonAssignmentCheck(
            AnnotatedTypeMirror varType,
            AnnotatedTypeMirror valueType,
            Tree valueTree,
            String errorKey) {

        if (varType.getKind() != valueType.getKind()) {
            checkUnsafeWidening(valueTree, valueType);
        }
        super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
    }

    /**
     * @param valueTree
     * @param valueType type of the to be widened node
     */
    private void checkUnsafeWidening(Tree valueTree, AnnotatedTypeMirror valueType) {
        if (valueType.getUnderlyingType().getKind() == TypeKind.BYTE) {
            AnnotationMirror valueAnno = valueType.getAnnotationInHierarchy(UNKNOWNVAL);
            if (isIntValRange(valueAnno)) {
                Range range = ValueAnnotatedTypeFactory.getRange(valueAnno);
                if (CastRangeUtil.isUnsignedByte(range)) {
                    checker.reportWarning(valueTree, "widening.unsafe", valueType, valueType);
                }
            }
        }

        if (valueType.getUnderlyingType().getKind() == TypeKind.SHORT) {
            AnnotationMirror valueAnno = valueType.getAnnotationInHierarchy(UNKNOWNVAL);
            if (isIntValRange(valueAnno)) {
                Range range = ValueAnnotatedTypeFactory.getRange(valueAnno);
                if (CastRangeUtil.isUnsignedShort(range)) {
                    checker.reportWarning(valueTree, "widening.unsafe", valueType, valueType);
                }
            }
        }
    }

    @Override
    public Void visitTypeCast(TypeCastTree node, Void p) {
        AnnotatedTypeMirror castType = atypeFactory.getAnnotatedType(node);
        AnnotatedTypeMirror exprType = atypeFactory.getAnnotatedType(node.getExpression());
        AnnotationMirror castAnno = castType.getAnnotationInHierarchy(UNKNOWNVAL);
        AnnotationMirror exprAnno = exprType.getAnnotationInHierarchy(UNKNOWNVAL);

        if (castAnno != null
                && exprAnno != null
                && isIntValRange(castAnno)
                && isIntValRangeOrUnknown(exprAnno)) {
//        	if (castType.getUnderlyingType().getKind() == TypeKind.BYTE) {
//        		AnnotationMirror signed_byte = atypeFactory.createIntRangeAnnotation(CastRangeUtil.signedByteRange());
//        		AnnotationMirror unsigned_byte = atypeFactory.createIntRangeAnnotation(CastRangeUtil.unsignedByteRange());
//        	}
//        	if (castType.getUnderlyingType().getKind() == TypeKind.SHORT) {
//        		AnnotationMirror signed_short = atypeFactory.createIntRangeAnnotation(CastRangeUtil.signedShortRange());
//        		AnnotationMirror unsigned_short = atypeFactory.createIntRangeAnnotation(CastRangeUtil.unsignedShortRange());
//        	}
            if (!isTypeCastSafe(castType, exprType)) {
                checker.reportError(
                        node, "cast.unsafe", exprType.toString(true), castType.toString(true));
            }
            return p;
        }

        return super.visitTypeCast(node, p);
    }

    private boolean isIntValRange(AnnotationMirror anno) {
        if (atypeFactory.isIntRange(anno) || AnnotationUtils.areSameByClass(anno, IntVal.class)) {
            return true;
        }
        return false;
    }

    private boolean isIntValRangeOrUnknown(AnnotationMirror anno) {
        if (atypeFactory.isIntRange(anno)
                || AnnotationUtils.areSameByClass(anno, IntVal.class)
                || AnnotationUtils.areSame(anno, UNKNOWNVAL)) {
            return true;
        }
        return false;
    }

    @Override
    public Void visitBinary(BinaryTree node, Void p) {
        ExpressionTree left = node.getLeftOperand();
        ExpressionTree right = node.getRightOperand();

        AnnotatedTypeMirror leftType = atypeFactory.getAnnotatedType(left);
        AnnotatedTypeMirror rightType = atypeFactory.getAnnotatedType(right);
        AnnotationMirror leftAnno = leftType.getAnnotationInHierarchy(UNKNOWNVAL);
        AnnotationMirror rightAnno = rightType.getAnnotationInHierarchy(UNKNOWNVAL);

        switch (node.getKind()) {
            case AND:
                if (leftType.getUnderlyingType().getKind() == TypeKind.BYTE
                                && right.toString().equals("255")
                        || rightType.getUnderlyingType().getKind() == TypeKind.BYTE
                                && left.toString().equals("255")) {
                    break;
                }
                if (leftType.getUnderlyingType().getKind() == TypeKind.SHORT
                                && right.toString().equals("65535")
                        || rightType.getUnderlyingType().getKind() == TypeKind.SHORT
                                && left.toString().equals("65535")) {
                    break;
                }
            case OR:
            case XOR:
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case REMAINDER:
                if (leftType.getUnderlyingType().getKind() == TypeKind.BYTE
                        && rightType.getUnderlyingType().getKind() == TypeKind.BYTE) {
                    if ((CastRangeUtil.isSignedByte(ValueAnnotatedTypeFactory.getRange(leftAnno))
                                    && CastRangeUtil.isUnsignedByte(
                                            ValueAnnotatedTypeFactory.getRange(rightAnno)))
                            || (CastRangeUtil.isUnsignedByte(
                                            ValueAnnotatedTypeFactory.getRange(leftAnno))
                                    && CastRangeUtil.isSignedByte(
                                            ValueAnnotatedTypeFactory.getRange(rightAnno)))) {
                        checker.reportError(
                                node, "binary.signedness.mismatch", leftAnno, rightAnno);
                        break;
                    }
                }

                if (leftType.getUnderlyingType().getKind() == TypeKind.SHORT
                        && rightType.getUnderlyingType().getKind() == TypeKind.SHORT) {
                    if ((CastRangeUtil.isSignedShort(ValueAnnotatedTypeFactory.getRange(leftAnno))
                                    && CastRangeUtil.isUnsignedShort(
                                            ValueAnnotatedTypeFactory.getRange(rightAnno)))
                            || (CastRangeUtil.isUnsignedShort(
                                            ValueAnnotatedTypeFactory.getRange(leftAnno))
                                    && CastRangeUtil.isSignedShort(
                                            ValueAnnotatedTypeFactory.getRange(rightAnno)))) {
                        checker.reportError(
                                node, "binary.signedness.mismatch", leftAnno, rightAnno);
                        break;
                    }
                }

                if (leftType.getUnderlyingType().getKind() == TypeKind.BYTE
                        || leftType.getUnderlyingType().getKind() == TypeKind.SHORT) {
                    checkUnsafeWidening(left, leftType);
                }
                if (rightType.getUnderlyingType().getKind() == TypeKind.BYTE
                        || rightType.getUnderlyingType().getKind() == TypeKind.SHORT) {
                    checkUnsafeWidening(right, rightType);
                }
                break;
            default:
                break;
        }

        return super.visitBinary(node, p);
    }
}
