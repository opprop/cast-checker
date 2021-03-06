package cast;

import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.common.value.ValueQualifierHierarchy;
import org.checkerframework.common.value.ValueTreeAnnotator;
import org.checkerframework.common.value.ValueTypeAnnotator;
import org.checkerframework.common.value.qual.IntVal;
import org.checkerframework.common.value.util.NumberUtils;
import org.checkerframework.common.value.util.Range;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedPrimitiveType;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.LiteralTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.PropagationTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

public class CastAnnotatedTypeFactory extends ValueAnnotatedTypeFactory {

    public CastAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        if (this.getClass().equals(CastAnnotatedTypeFactory.class)) {
            this.postInit();
        }
    }

    protected static final Set<String> COVERED_CLASS_STRINGS =
            Collections.unmodifiableSet(
                    new HashSet<>(
                            Arrays.asList(
                                    "int",
                                    "java.lang.Integer",
                                    "double",
                                    "java.lang.Double",
                                    "byte",
                                    "java.lang.Byte",
                                    "java.lang.String",
                                    "char",
                                    "java.lang.Character",
                                    "float",
                                    "java.lang.Float",
                                    "boolean",
                                    "java.lang.Boolean",
                                    "long",
                                    "java.lang.Long",
                                    "short",
                                    "java.lang.Short",
                                    "char[]")));
    
    @Override
    public CFTransfer createFlowTransferFunction(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        return new CastTransfer(analysis);
    }
    
    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new CastQualifierHierarchy(factory, this);
    }
    
    private class CastQualifierHierarchy extends ValueQualifierHierarchy {

    	/** The type factory to use. */
        final CastAnnotatedTypeFactory atypeFactory;
        
		public CastQualifierHierarchy(MultiGraphFactory factory, CastAnnotatedTypeFactory atypeFactory) {
			super(factory, atypeFactory);
			this.atypeFactory = atypeFactory;
		}
		
		@Override
	    public AnnotationMirror widenedUpperBound(
	            AnnotationMirror newQualifier, AnnotationMirror previousQualifier) {
			AnnotationMirror lub = leastUpperBound(newQualifier, previousQualifier);
	        if (AnnotationUtils.areSameByName(lub, ValueAnnotatedTypeFactory.INTRANGE_NAME)) {
	            Range lubRange = ValueAnnotatedTypeFactory.getRange(lub);
	            Range newRange = ValueAnnotatedTypeFactory.getRange(newQualifier);
	            Range oldRange = ValueAnnotatedTypeFactory.getRange(previousQualifier);
	            Range wubRange = widenedRange(newRange, oldRange, lubRange);
	            return atypeFactory.createIntRangeAnnotation(wubRange);
	        } else if (AnnotationUtils.areSameByName(
	                lub, ValueAnnotatedTypeFactory.ARRAYLENRANGE_NAME)) {
	            Range lubRange = ValueAnnotatedTypeFactory.getRange(lub);
	            Range newRange = ValueAnnotatedTypeFactory.getRange(newQualifier);
	            Range oldRange = ValueAnnotatedTypeFactory.getRange(previousQualifier);
	            Range wubRange = widenedRange(newRange, oldRange, lubRange);
	            return atypeFactory.createArrayLenRangeAnnotation(wubRange);
	        } else {
	            return lub;
	        }
	    }
		
		private Range widenedRange(Range newRange, Range oldRange, Range lubRange) {
	        if (newRange == null || oldRange == null || lubRange.equals(oldRange)) {
	            return lubRange;
	        }
	        // If both bounds of the new range are bigger than the old range, then returned range
	        // should use the lower bound of the new range and a MAX_VALUE.
	        if ((newRange.from >= oldRange.from && newRange.to >= oldRange.to)) {
	            long max = lubRange.to;
	            if (max < Byte.MAX_VALUE) {
	                max = Byte.MAX_VALUE;
	            } else if (max < Short.MAX_VALUE) {
	                max = Short.MAX_VALUE;
	            } else if (max < Integer.MAX_VALUE) {
	                max = Integer.MAX_VALUE;
	            } else {
	                max = Integer.MAX_VALUE;
	            }
	            return Range.create(newRange.from, max);
	        }

	        // If both bounds of the old range are bigger than the new range, then returned range
	        // should use a MIN_VALUE and the upper bound of the new range.
	        if ((newRange.from <= oldRange.from && newRange.to <= oldRange.to)) {
	            long min = lubRange.from;
	            if (min > Byte.MIN_VALUE) {
	                min = Byte.MIN_VALUE;
	            } else if (min > Short.MIN_VALUE) {
	                min = Short.MIN_VALUE;
	            } else if (min > Integer.MIN_VALUE) {
	                min = Integer.MIN_VALUE;
	            } else {
	                min = Integer.MIN_VALUE;
	            }
	            return Range.create(min, newRange.to);
	        }

	        if (lubRange.isWithin(Byte.MIN_VALUE + 1, Byte.MAX_VALUE)
	                || lubRange.isWithin(Byte.MIN_VALUE, Byte.MAX_VALUE - 1)) {
	            return Range.BYTE_EVERYTHING;
	        } else if (lubRange.isWithin(Short.MIN_VALUE + 1, Short.MAX_VALUE)
	                || lubRange.isWithin(Short.MIN_VALUE, Short.MAX_VALUE - 1)) {
	            return Range.SHORT_EVERYTHING;
	        } else if (lubRange.isWithin(Long.MIN_VALUE + 1, Long.MAX_VALUE)
	                || lubRange.isWithin(Long.MIN_VALUE, Long.MAX_VALUE - 1)) {
	            return Range.INT_EVERYTHING;
	        } else {
	            return Range.INT_EVERYTHING;
	        }
	    }
    }

    @Override
    protected TypeAnnotator createTypeAnnotator() {
        return new ListTypeAnnotator(new CastTypeAnnotator(this), super.createTypeAnnotator());
    }
    

    /**
     * Performs pre-processing on annotations written by users, replacing illegal annotations by
     * legal ones.
     */
    private class CastTypeAnnotator extends ValueTypeAnnotator {

        private CastTypeAnnotator(ValueAnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        protected Void scan(AnnotatedTypeMirror type, Void aVoid) {
            return super.scan(type, aVoid);
        }

        @Override
        public Void visitExecutable(AnnotatedExecutableType t, Void p) {
            List<AnnotatedTypeMirror> paramTypes = t.getParameterTypes();
            for (AnnotatedTypeMirror paramType : paramTypes) {
                AnnotationMirror anno = createIntRangeAnnotations(paramType);
                if (anno != null) {
                    paramType.addMissingAnnotations(Collections.singleton(anno));
                }
            }

            AnnotatedTypeMirror retType = t.getReturnType();
            AnnotationMirror anno = createIntRangeAnnotations(retType);
            if (anno != null) {
                retType.addMissingAnnotations(Collections.singleton(anno));
            }
            while (retType instanceof AnnotatedArrayType) {
            	retType = ((AnnotatedArrayType) retType).getComponentType();
                anno = createIntRangeAnnotations(retType);
                if (anno != null) {
                	retType.addMissingAnnotations(Collections.singleton(anno));
                }
            }

            return super.visitExecutable(t, p);
        }
    }

    @Override
    public void addComputedTypeAnnotations(Element elt, AnnotatedTypeMirror type) {
        if (elt.getKind() == ElementKind.FIELD
        		|| elt.getKind() == ElementKind.PARAMETER
        		|| (elt.getKind() == ElementKind.LOCAL_VARIABLE && type instanceof AnnotatedArrayType)) {
        	CastTreeTypeAnnotator visitor = new CastTreeTypeAnnotator(this);
            visitor.visit(type);
        }

        super.addComputedTypeAnnotations(elt, type);
    }

    private class CastTreeTypeAnnotator extends ValueTypeAnnotator {

        private CastTreeTypeAnnotator(ValueAnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        protected Void scan(AnnotatedTypeMirror type, Void aVoid) {
            return super.scan(type, aVoid);
        }

        @Override
        public Void visitArray(AnnotatedArrayType t, Void p) {
            AnnotatedTypeMirror comp = t.getComponentType();
            AnnotationMirror anno = createIntRangeAnnotations(comp);
            if (anno != null) {
                comp.addMissingAnnotations(Collections.singleton(anno));
            }
            return super.visitArray(t, p);
        }
        
        @Override
        public Void visitPrimitive(AnnotatedPrimitiveType t, Void p) {
            AnnotationMirror anno = createIntRangeAnnotations(t);
            if (anno != null) {
                t.addMissingAnnotations(Collections.singleton(anno));
            }
            return super.visitPrimitive(t, p);
        }
    }

    private AnnotationMirror createIntRangeAnnotations(AnnotatedTypeMirror atm) {
        AnnotationMirror newAnno;
        switch (atm.getKind()) {
	        case BYTE:
	            newAnno = createIntRangeAnnotation(Range.BYTE_EVERYTHING);
	            break;
            case SHORT:
                newAnno = createIntRangeAnnotation(Range.SHORT_EVERYTHING);
                break;
            case CHAR:
                newAnno = createIntRangeAnnotation(Range.CHAR_EVERYTHING);
                break;
            case INT:
                newAnno = createIntRangeAnnotation(Range.INT_EVERYTHING);
                break;
            case DOUBLE:
            case FLOAT:
            case LONG:
                newAnno = createIntRangeAnnotation(Range.EVERYTHING);
                break;
            default:
                newAnno = null;
        }
        return newAnno;
    }
    
    private AnnotationMirror createIntRangeAnnotationsForLocalVaraible(AnnotatedTypeMirror atm) {
        AnnotationMirror newAnno;
        switch (atm.getKind()) {
            case CHAR:
                newAnno = createIntRangeAnnotation(Range.CHAR_EVERYTHING);
                break;
        	case BYTE:
        		newAnno = createIntRangeAnnotation(Range.create(Byte.MIN_VALUE, Byte.MAX_VALUE*2+1));
        		break;
            case SHORT:
                newAnno = createIntRangeAnnotation(Range.create(Short.MIN_VALUE, Short.MAX_VALUE*2+1));
                break;
            case INT:
                newAnno = createIntRangeAnnotation(Range.INT_EVERYTHING);
                break;
            case DOUBLE:
            case FLOAT:
            case LONG:
                newAnno = createIntRangeAnnotation(Range.EVERYTHING);
                break;
            default:
                newAnno = null;
        }
        return newAnno;
    }

    @Override
    protected TreeAnnotator createTreeAnnotator() {
        // Don't call super.createTreeAnnotator because it includes the PropagationTreeAnnotator.
        // Only use the PropagationTreeAnnotator for typing new arrays.  The Value Checker
        // computes types differently for all other trees normally typed by the
        // PropagationTreeAnnotator.
        TreeAnnotator arrayCreation =
                new TreeAnnotator(this) {
                    PropagationTreeAnnotator propagationTreeAnnotator =
                            new PropagationTreeAnnotator(atypeFactory);

                    @Override
                    public Void visitNewArray(NewArrayTree node, AnnotatedTypeMirror mirror) {
                    	return propagationTreeAnnotator.visitNewArray(node, mirror);
                    }
                };
        return new ListTreeAnnotator(
                new CastTreeAnnotator(this),
                new LiteralTreeAnnotator(this).addStandardLiteralQualifiers(),
                arrayCreation);
    }

    protected class CastTreeAnnotator extends ValueTreeAnnotator {
        public CastTreeAnnotator(CastAnnotatedTypeFactory factory) {
            super(factory);
        }

        @Override
		public Void visitVariable(VariableTree tree, AnnotatedTypeMirror atm) {
        	if (TreeUtils.isLocalVariable(tree)) {
        		AnnotationMirror anno = createIntRangeAnnotationsForLocalVaraible(atm);
                if (anno != null) {
                    atm.addMissingAnnotations(Collections.singleton(anno));
                }
        	}
        	return super.visitVariable(tree, atm);
        }

        @Override
        public Void visitTypeCast(TypeCastTree tree, AnnotatedTypeMirror atm) {
            if (!handledByValueChecker(atm)) {
                return super.visitTypeCast(tree, atm);
            }
            
            AnnotationMirror oldAnno =
                    getAnnotatedType(tree.getExpression()).getAnnotationInHierarchy(UNKNOWNVAL);
            if (oldAnno == null) {
                return super.visitTypeCast(tree, atm);
            }

            TypeMirror newType = atm.getUnderlyingType();
            AnnotationMirror newAnno;
            Range range = Range.EVERYTHING;
            Class<?> newClass = ValueCheckerUtils.getClassFromType(newType);

            if (isIntRange(oldAnno) && (range = getRange(oldAnno)).isWiderThan(MAX_VALUES)) {
                Range castRange = getRange(oldAnno);
                if (newClass == byte.class && CastRangeUtil.isUnsignedByte(castRange)) {
                    newAnno = createIntRangeAnnotation(CastRangeUtil.unsignedByteRange());
                } else if (newClass == short.class && CastRangeUtil.isUnsignedShort(castRange)) {
                    newAnno = createIntRangeAnnotation(CastRangeUtil.unsignedShortRange());
                } else if (newClass == String.class) {
                    newAnno = UNKNOWNVAL;
                } else if (newClass == Boolean.class || newClass == boolean.class) {
                    throw new UnsupportedOperationException(
                            "ValueAnnotatedTypeFactory: can't convert int to boolean");
                } else {
                    newAnno = createIntRangeAnnotation(NumberUtils.castRange(newType, range));
                }
                atm.addMissingAnnotations(Collections.singleton(newAnno));
                return null;
            } else if (AnnotationUtils.areSameByClass(oldAnno, IntVal.class)
                    && newClass != double.class
                    && newClass != float.class) {
                List<Long> longs = ValueAnnotatedTypeFactory.getIntValues(oldAnno);
                List<?> values = castNumbers(newType, longs);
                newAnno = createResultingAnnotation(atm.getUnderlyingType(), values);
                atm.addMissingAnnotations(Collections.singleton(newAnno));
                return null;
            } else if ((newClass == byte.class || newClass == short.class || newClass == char.class || newClass == int.class)) {
                newAnno = createIntRangeAnnotation(NumberUtils.castRange(newType, range));
                atm.addMissingAnnotations(Collections.singleton(newAnno));
            }

            return super.visitTypeCast(tree, atm);
        }
    }

    /** Converts a {@code List<A>} to a {@code List<B>}, where A and B are numeric types. */
    private List<? extends Number> castNumbers(TypeMirror type, List<? extends Number> numbers) {
        if (numbers == null) {
            return null;
        }

        List<Long> values = new ArrayList<>();

        javax.lang.model.type.TypeKind typeKind = type.getKind();
        switch (typeKind) {
            case BYTE:
                for (Number l : numbers) {
                    if (l.longValue() > 255 || l.longValue() < -128) {
                        values.add((long) l.byteValue());
                    } else {
                        values.add(l.longValue());
                    }
                }
                return values;
            case SHORT:
                for (Number l : numbers) {
                    if (l.longValue() > 65535 || l.longValue() < -32768) {
                        values.add((long) l.shortValue());
                    } else {
                        values.add(l.longValue());
                    }
                }
                return values;
            case CHAR:
            case INT:
            case LONG:
                for (Number l : numbers) {
                    values.add(l.longValue());
                }
                return values;
            default:
                throw new UnsupportedOperationException(typeKind.toString());
        }
    }

    /**
     * Returns a constant value annotation with the {@code values}. The class of the annotation
     * reflects the {@code resultType} given.
     *
     * @param resultType used to selected which kind of value annotation is returned
     * @param values must be a homogeneous list: every element of it has the same class
     * @return a constant value annotation with the {@code values}
     */
    AnnotationMirror createResultingAnnotation(TypeMirror resultType, List<?> values) {
        if (values == null) {
            return UNKNOWNVAL;
        }
        // For some reason null is included in the list of values,
        // so remove it so that it does not cause a NPE elsewhere.
        values.remove(null);
        if (values.isEmpty()) {
            return BOTTOMVAL;
        }

        switch (resultType.getKind()) {
            case INT:
            case LONG:
            case SHORT:
            case BYTE:
                List<Number> numberVals = new ArrayList<>(values.size());
                List<Character> characterVals = new ArrayList<>(values.size());
                for (Object o : values) {
                    if (o instanceof Character) {
                        characterVals.add((Character) o);
                    } else {
                        numberVals.add((Number) o);
                    }
                }
                if (numberVals.isEmpty()) {
                    return createCharAnnotation(characterVals);
                }
                return createNumberAnnotationMirror(new ArrayList<>(numberVals));
            case CHAR:
                List<Character> charVals = new ArrayList<>(values.size());
                for (Object o : values) {
                    if (o instanceof Number) {
                        charVals.add((char) ((Number) o).intValue());
                    } else {
                        charVals.add((char) o);
                    }
                }
                return createCharAnnotation(charVals);
            default:
                throw new UnsupportedOperationException("Unexpected kind:" + resultType);
        }
    }

    /** Returns true iff the given type is in the domain of the Constant Value Checker. */
    private boolean handledByValueChecker(AnnotatedTypeMirror type) {
        return COVERED_CLASS_STRINGS.contains(type.getUnderlyingType().toString());
    }
}
