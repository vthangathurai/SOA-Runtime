/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.external;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * @author rkulandaivel
 *
 * This class sorts the contents of JDefinedClass. 
 * It access the datastructures of JDefinedClass through reflection and does the sorting.
 */
public class ObjectFactoryClassContentsSorter {
	private static CallTrackingLogger s_logger = LogManager.getInstance(AxisJavaWSDLGeneratorImpl.class);
	private static final ObjectFactoryClassContentsSorter s_instance = new ObjectFactoryClassContentsSorter();

	private volatile boolean intialized = false;
	private Field m_annotationsField = null;
	private Field m_constructorsField = null;
	private Field m_annotationsFieldOfJMethod = null;
	private Field m_annotationsFieldOfJFieldVar = null;
	private Field m_classesField = null;
	private Field m_fieldsField = null;

	private ObjectFactoryClassContentsSorter(){
		
	}
	public static final ObjectFactoryClassContentsSorter getInstance(){
		if( !s_instance.intialized ){
			synchronized (ObjectFactoryClassContentsSorter.class) {
				if( !s_instance.intialized ){
					s_instance.init();
					s_instance.intialized = true;
				}
			}
		}
		return s_instance;
	}

	@SuppressWarnings("rawtypes")
	public void init(){
		Class jDefinedClazz = JDefinedClass.class;
		Class jMethodClazz = JMethod.class;

		try {
			m_annotationsField = jDefinedClazz.getDeclaredField("annotations");
			m_constructorsField = jDefinedClazz.getDeclaredField("constructors");
			m_annotationsFieldOfJMethod = jMethodClazz.getDeclaredField("annotations");
			m_classesField = jDefinedClazz.getDeclaredField("classes");
			m_fieldsField = jDefinedClazz.getDeclaredField("fields");
			m_annotationsFieldOfJFieldVar = JVar.class.getDeclaredField("annotations");
			
			m_annotationsField.setAccessible(true);
			m_constructorsField.setAccessible(true);
			m_annotationsFieldOfJMethod.setAccessible(true);
			m_classesField.setAccessible(true);
			m_fieldsField.setAccessible(true);
			m_annotationsFieldOfJFieldVar.setAccessible(true);
		} catch (SecurityException e) {
			throw new RuntimeException("Exception while retriveing the fields of com.sun.codemodel.JDefinedClass through reflection", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Exception while retriveing the fields of com.sun.codemodel.JDefinedClass through reflection", e);
		}
	}

	private Object getReflectionValue( Field field, Object instanceObj){
		Object val = null;
		try {
			val = field.get(instanceObj);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Exception while retriveing the value for field " + field.getName() + " through reflection", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while retriveing the value for field " + field.getName() + " through reflection", e);
		}
		return val;
	}

	private void sortAnnotations(List<JAnnotationUse> anotations){
		Collections.sort( anotations, JClassContentsComparatorProvider.getAnnotationUseComparatorInstance() );
	}

	@SuppressWarnings("unchecked")
	private void sortAnnotations(JDefinedClass jDefinedClazzObj){
		s_logger.log(Level.INFO, "Sorting the Annotations - Start");
		List<JAnnotationUse> annotations = (List<JAnnotationUse>) getReflectionValue( m_annotationsField, jDefinedClazzObj);

		if(annotations != null){
			sortAnnotations(annotations);					
		}
		s_logger.log(Level.INFO, "Sorting the Annotations - End");
	}

	@SuppressWarnings("unused")
	private void sortInterfaces(JDefinedClass jDefinedClazzObj){
		//no implementation
		//the interfaces field is a TreeSet. It follows natural ordering
	}

	@SuppressWarnings("unused")
	private void sortEnumConstantsByName(JDefinedClass jDefinedClazzObj){
		//no implementation
		//the enumConstantsByName field is a LinkedHashMap. It should be the order present in xml.
		//so dont touch
	}

	@SuppressWarnings("unchecked")
	private void sortFields(JDefinedClass jDefinedClazzObj){
		//Ideally there should not be any implementation
		//because the fields field is a LinkedHashMap. It should be the order present in xml.

		//but as far as ObjectFactory is concerned fields need to be sorted 
		//because it does not have any significance of propOrder 
		
		s_logger.log(Level.INFO, "Sorting the Fields Vars - Start");

		Map<String,JFieldVar> fields = (Map<String,JFieldVar>) getReflectionValue( m_fieldsField, jDefinedClazzObj);

		if( fields != null) {
			List<JFieldVar> fieldsList = new ArrayList<JFieldVar>( fields.size() );

			Iterator<Map.Entry<String, JFieldVar>> entryIterator = fields.entrySet().iterator();
			while( entryIterator.hasNext() ){
				JFieldVar jFieldVar = entryIterator.next().getValue();
				fieldsList.add( jFieldVar );
				entryIterator.remove();
				
				List<JAnnotationUse> annotations = (List<JAnnotationUse>) getReflectionValue( m_annotationsFieldOfJFieldVar, jFieldVar);

				if(annotations != null){
					sortAnnotations(annotations);					
				}
			}

			Collections.sort(fieldsList, JClassContentsComparatorProvider.getJFieldVarComparatorInstance() );

			for( JFieldVar jFieldVar :  fieldsList){
				fields.put(jFieldVar.name(), jFieldVar);
			}			
		}

		s_logger.log(Level.INFO, "Sorting the Fields Vars - End");
	}

	@SuppressWarnings("unchecked")
	private void sortContentsOfJMethod(List<JMethod> jMethods){
		for( JMethod jmethod: jMethods){
			//sorting only the annotations of a method and 
			// obviously no need to sort order of params, exceptions definitions 
			List<JAnnotationUse> annotations = (List<JAnnotationUse>) getReflectionValue( m_annotationsFieldOfJMethod, jmethod);

			if(annotations != null){
				sortAnnotations(annotations);					
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void sortConstructors(JDefinedClass jDefinedClazzObj){
		s_logger.log(Level.INFO, "Sorting the Constructors - Start");
		List<JMethod> constructors = (List<JMethod>) getReflectionValue( m_constructorsField, jDefinedClazzObj);

		if( constructors != null ){
			Collections.sort( constructors, JClassContentsComparatorProvider.getSameNameJMethodsComparatorInstance() );
			sortContentsOfJMethod( constructors );
		}
		s_logger.log(Level.INFO, "Sorting the Constructors - End");
	}

	private void sortMethods(JDefinedClass jDefinedClazzObj){
		s_logger.log(Level.INFO, "Sorting the Methods - Start");
		List<JMethod> jMethods = (List<JMethod>) jDefinedClazzObj.methods();
		
		if( jMethods != null ){
			Collections.sort( jMethods, JClassContentsComparatorProvider.getJMethodComparatorInstance() );
			
			sortContentsOfJMethod( jMethods );
		}
		s_logger.log(Level.INFO, "Sorting the Methods - End");

	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void sortInnerClasses(JDefinedClass jDefinedClazzObj){
		List<JDefinedClass> classes = (List<JDefinedClass>) getReflectionValue( m_classesField, jDefinedClazzObj);

		if(classes != null){
			//no need to sort the order of class but the contents
			// (classes is a TreeMap and natural sorting is there)  
			
			for( JDefinedClass clazz : classes){
				sort( clazz );
			}					
		}

	}
	
	public void sort(JDefinedClass jDefinedClazz){
		//only Annotations, Fields, Constructors and Methods 
		//are sorted here because ObjectFactory only use those
		//others like Interfaces, InnerClass are empty
		
		String clazzFullName = jDefinedClazz.fullName();
		s_logger.log(Level.INFO, "Sorting the contents of class  " + clazzFullName );
		long stTime = System.currentTimeMillis();
		try {
			sortAnnotations(jDefinedClazz);
			sortFields(jDefinedClazz);
			sortConstructors(jDefinedClazz);
			sortMethods(jDefinedClazz);
		} catch (ClassCastException e) {
			throw new RuntimeException("This error could be due to the change in the version of jars xjc/axis.", e);
		}
		long endTime = System.currentTimeMillis();
		s_logger.log(Level.INFO, "Successfully sorted the contents of class  " + clazzFullName);
		s_logger.log(Level.INFO, "Times taken sorting class '"+clazzFullName+"' is "+ ((stTime-endTime)/1000) +" secs");
	}
	
}
