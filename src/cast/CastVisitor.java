package cast;

import com.github.javaparser.JavaToken.Kind;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueVisitor;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;
import org.checkerframework.common.value.qual.UnknownVal;
import org.checkerframework.common.value.util.NumberUtils;
import org.checkerframework.common.value.util.Range;
import org.checkerframework.dataflow.cfg.node.BinaryOperationNode;
import org.checkerframework.dataflow.cfg.node.BitwiseAndNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.cfg.node.WideningConversionNode;
import org.checkerframework.framework.flow.CFValue;
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
    		checkUnsafeWidening(valueTree, varType, valueType);
    	}
        super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
    }
    
    /**
     * 
     * @param valueTree
     * @param varType type of the node to be widened to
     * @param valueType type of the to be widened node
     */
    private void checkUnsafeWidening(Tree valueTree, AnnotatedTypeMirror varType, AnnotatedTypeMirror valueType) {
    	if (valueType.getUnderlyingType().getKind() == TypeKind.BYTE) {
            AnnotationMirror valueAnno = valueType.getAnnotationInHierarchy(UNKNOWNVAL);
            if (isIntValRange(valueAnno)) {
                Range range = ValueAnnotatedTypeFactory.getRange(valueAnno);
                if (CastRangeUtil.isUnsignedByte(range)) {
                    checker.reportWarning(valueTree, "widening.unsafe", valueType, varType);
                }
            }
        }
        
        if (valueType.getUnderlyingType().getKind() == TypeKind.SHORT) {
            AnnotationMirror valueAnno = valueType.getAnnotationInHierarchy(UNKNOWNVAL);
            if (isIntValRange(valueAnno)) {
                Range range = ValueAnnotatedTypeFactory.getRange(valueAnno);
                if (CastRangeUtil.isUnsignedShort(range)) {
                    checker.reportWarning(valueTree, "widening.unsafe", valueType, varType);
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
    	if (atypeFactory.isIntRange(anno) || AnnotationUtils.areSameByClass(anno, IntVal.class) || AnnotationUtils.areSame(anno, UNKNOWNVAL)) {
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
        
    	if (node.getKind() == Tree.Kind.AND) {
    		if (leftType.getUnderlyingType().getKind() == TypeKind.BYTE && right.toString().equals("255")
    				|| rightType.getUnderlyingType().getKind() == TypeKind.BYTE && left.toString().equals("255")) {
    			return super.visitBinary(node, p);
    		}
    	}
    	
    	if (node.getKind() == Tree.Kind.AND) {
    		if (leftType.getUnderlyingType().getKind() == TypeKind.SHORT && right.toString().equals("65535")
    				|| rightType.getUnderlyingType().getKind() == TypeKind.SHORT && left.toString().equals("65535")) {
    			return super.visitBinary(node, p);
    		}
    	}
    	
    	AnnotationMirror leftAnno = leftType.getAnnotationInHierarchy(UNKNOWNVAL);
        AnnotationMirror rightAnno = rightType.getAnnotationInHierarchy(UNKNOWNVAL);
        
    	if (leftType.getUnderlyingType().getKind() == TypeKind.BYTE || leftType.getUnderlyingType().getKind() == TypeKind.SHORT) {
    		checkUnsafeWidening(node, rightType, leftType);
    		return super.visitBinary(node, p);
		}
    	
    	if (rightType.getUnderlyingType().getKind() == TypeKind.BYTE || rightType.getUnderlyingType().getKind() == TypeKind.SHORT) {
    		checkUnsafeWidening(node, leftType, rightType);
    		return super.visitBinary(node, p);
		}
    	
        if (leftType.getUnderlyingType().getKind() == TypeKind.BYTE && rightType.getUnderlyingType().getKind() == TypeKind.BYTE) {
        	if ((CastRangeUtil.isSignedByte(ValueAnnotatedTypeFactory.getRange(leftAnno)) 
        			&& CastRangeUtil.isUnsignedByte(ValueAnnotatedTypeFactory.getRange(rightAnno)))
        		|| (CastRangeUtil.isUnsignedByte(ValueAnnotatedTypeFactory.getRange(rightAnno))
        			&& CastRangeUtil.isSignedByte(ValueAnnotatedTypeFactory.getRange(leftAnno)))) {
        		checker.reportError(node,
                        "comparison.unit.mismatch",
                        leftAnno,
                        rightAnno);
        	}
        	return super.visitBinary(node, p);
        }
        
        if (leftType.getUnderlyingType().getKind() == TypeKind.SHORT && rightType.getUnderlyingType().getKind() == TypeKind.SHORT) {
        	if ((CastRangeUtil.isSignedShort(ValueAnnotatedTypeFactory.getRange(leftAnno)) 
        			&& CastRangeUtil.isUnsignedShort(ValueAnnotatedTypeFactory.getRange(rightAnno)))
        		|| (CastRangeUtil.isUnsignedShort(ValueAnnotatedTypeFactory.getRange(rightAnno))
        			&& CastRangeUtil.isSignedShort(ValueAnnotatedTypeFactory.getRange(leftAnno)))) {
        		checker.reportError(node,
                        "comparison.unit.mismatch",
                        leftAnno,
                        rightAnno);
        	}
        	return super.visitBinary(node, p);
        }
    	
        return super.visitBinary(node, p);
    }

//    private void visitBinaryOperation(BinaryOperationNode node) {
//        Node leftNode = node.getLeftOperand();
//        Node rightNode = node.getRightOperand();
//
//        if (leftNode instanceof WideningConversionNode
//                && (!(node instanceof BitwiseAndNode) || !rightNode.toString().equals("255"))) {
//            checkUnsafeWidening((WideningConversionNode) leftNode, node.getTree());
//        }
//        if (rightNode instanceof WideningConversionNode
//                && (!(node instanceof BitwiseAndNode) || !leftNode.toString().equals("255"))) {
//            checkUnsafeWidening((WideningConversionNode) rightNode, node.getTree());
//        }
//    }
//
//    private void checkUnsafeWidening(WideningConversionNode node, Tree target) {
//        if (isUnsighedByteWideningConversion(node)) {
//            AnnotatedTypeMirror targetType = atypeFactory.getAnnotatedType(target);
//            AnnotatedTypeMirror exprType = atypeFactory.getAnnotatedType(node.getTree());
//            checker.reportError(target, "cast.unsafe", exprType, targetType);
//        }
//    }
//
//    private boolean isUnsighedByteWideningConversion(WideningConversionNode node) {
//        if (node.getOperand().getType().getKind() == TypeKind.BYTE) {
//            CFValue operandValue = atypeFactory.getInferredValueFor(node.getOperand().getTree());
//            Set<AnnotationMirror> annos = operandValue.getAnnotations();
//            for (AnnotationMirror anno : annos) {
//                if (AnnotationUtils.areSameByClass(anno, IntRange.class)) {
//                    Range annoRange = ValueAnnotatedTypeFactory.getRange(anno);
//                    if (isUnsignedByte(annoRange)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
}
