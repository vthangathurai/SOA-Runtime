/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.external;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * @author rkulandaivel
 * 
 */
public class JClassContentsComparatorProvider {
	private static final JMethodsComparator s_jMethodsComparatorInstance = new JMethodsComparator();
	private static final JAnnotationUseComparator s_jAnnotationUseComparatorInstance = new JAnnotationUseComparator();
	private static final SameNameJMethodsComparator s_sameNameJMethodsComparatorInstance = new SameNameJMethodsComparator();
	private static final JFieldVarComparator s_JFieldVarComparatorInstance = new JFieldVarComparator();
	
	private JClassContentsComparatorProvider(){}
	public static final Comparator<JMethod> getJMethodComparatorInstance(){
		return s_jMethodsComparatorInstance;
	}

	public static final Comparator<JAnnotationUse> getAnnotationUseComparatorInstance(){
		return s_jAnnotationUseComparatorInstance;
	}

	public static final Comparator<JMethod> getSameNameJMethodsComparatorInstance(){
		return s_sameNameJMethodsComparatorInstance;
	}

	public static final Comparator<JFieldVar> getJFieldVarComparatorInstance(){
		return s_JFieldVarComparatorInstance;
	}

	private static Field getReflectionField( Class<?> clazz, String fieldName){
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
		} catch (SecurityException e) {
			throw new RuntimeException("Exception while retriveing the field '"+ fieldName +"' of '"+ clazz.getName() +"' through reflection", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Exception while retriveing the field '"+ fieldName +"' of '"+ clazz.getName() +"' through reflection", e);
		}
		return field;
	}

	private static Object getReflectionValue( Field field, Object instanceObj ){
		try {
			return field.get(instanceObj);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Exception while retriveing the value for field " + field.getName() + " through reflection", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while retriveing the value for field " + field.getName() + " through reflection", e);
		}
	}

	private static class SameNameJMethodsComparator implements Comparator<JMethod> {
		private Field m_paramsField = null;
		private SameNameJMethodsComparator(){
			m_paramsField = getReflectionField( JMethod.class,  "params");
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(JMethod object1, JMethod object2) {
			int returnResult;

			List<JVar> params1 = (List<JVar>) getReflectionValue( m_paramsField, object1);
			List<JVar> params2 = (List<JVar>) getReflectionValue( m_paramsField, object2);

			if( (params1 == null) && (params2 == null) ){
				returnResult = 0;

			}else if( params1 == null){
				returnResult = -1;

			}else if( params2 == null){
				returnResult = 1;
			}else{
				returnResult = params1.size() - params2.size() ;
			}
			return returnResult;
		}

	}

	private static class JMethodsComparator extends SameNameJMethodsComparator implements Comparator<JMethod> {

		private JMethodsComparator(){
			
		}
		@Override
		public int compare(JMethod object1, JMethod object2) {

			int nameCompare = object1.name().compareTo(object2.name());
			if( nameCompare == 0 ){
				nameCompare = super.compare(object1, object2);
			}
			return nameCompare;
		}

	}
	
	private static class JAnnotationUseComparator implements Comparator<JAnnotationUse> {

		private Field m_jClazzField = null;
		private JAnnotationUseComparator(){
			m_jClazzField = getReflectionField( JAnnotationUse.class,  "clazz");
		}
		@Override
		public int compare(JAnnotationUse object1, JAnnotationUse object2) {
			JClass clazz1 = (JClass) getReflectionValue( m_jClazzField, object1);
			JClass clazz2 = (JClass) getReflectionValue( m_jClazzField, object2);
			
			return clazz1.compareTo( clazz2);
		}
		
	}

	private static class JFieldVarComparator  implements Comparator<JFieldVar> {

		private JFieldVarComparator(){
			
		}
		@Override
		public int compare(JFieldVar object1, JFieldVar object2) {
			return object1.name().compareTo(object2.name());
		}

	}
}
