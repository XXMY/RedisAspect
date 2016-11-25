package cfw.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by Cfw on 2016/7/23.
 */
public class ReflectUtils {

    /**
     * Get generic type of specified method's return type in string.<br/>
     * If the return type of method is not generic, null will be returned.
     * @author Fangwei_Cai
     * @create 2016-7-23 15:56:32
     * @param method
     * @return
     */
    public static String getGenericTypeName(Method method){

        String genericTypeName = null;

        String returnTypeName = method.getGenericReturnType().toString();
        if(returnTypeName.matches(".*<.*>")){
            genericTypeName = returnTypeName.substring(returnTypeName.indexOf("<")+1, returnTypeName.indexOf(">"));
        }

        return genericTypeName;
    }

    /**
     * Get class from specified class type name.<br/>
     * If the class specified not exists, null will be returned.
     * @author Fangwei_Cai
     * @time since 2016-10-14 18:33:42
     * @param className
     * @return
     */
    public static Class getGenericType(String className){
        try {
            Class genericTypeClass = Class.forName(className);
            return genericTypeClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get generic type of specified method's return type.<br/>
     * If the return type of method is not generic, null will be returned.
     * @author Fangwei_Cai
     * @time since 2016-10-14 18:27:39
     * @param method
     * @return
     */
    public static Class getGenericType(Method method){
        String genericTypeName = ReflectUtils.getGenericTypeName(method);

        return ReflectUtils.getGenericType(genericTypeName);
    }

    /**
     * Use visible field to create corresponding setter method.
     * @author Fangwei_Cai
     * @create 2016年4月24日 下午4:24:43
     * @modified 2016年7月11日14:24:34
     * @param field
     * @param isGet False in default to create Setter method.
     * @return
     */
    public static String createMethodName(Field field, boolean isGet){
        // Get the property type/identifier first then create the method name.
        Class<?> identifer = field.getType();
        String simpleIdentiferName = identifer.getSimpleName();

        String attributeName = field.getName();

        String methodName = null;
        char [] attributeNameChar = attributeName.toCharArray();
        attributeNameChar[0] = attributeName.toUpperCase().charAt(0);

        String upperAttributeName = new String(attributeNameChar);

        if(isGet){
            // To create Getter method.
            if(isIdentifierHas(simpleIdentiferName)){
                methodName = "is";
            }else{
                methodName = "get";
            }
            methodName += upperAttributeName;
        }else{
            // To create Setter method
            methodName = "set"+upperAttributeName;
        }

        return methodName;
    }

    /**
     * <b>Main method to assign</b>.<p>
     * Attempting to use this method, something should keep in mind is that:<br>
     * 1: This method just invoke given object's setter method to assign value,<br>
     * and a general setter method always need one parameter, you should observe this.<br>
     * 2: Second but important. You should use your IDE to create your POJO's getter or <br>
     * setter methods. It's important here. We use PO's attribute name to find its<br>
     * setter method, for example, if attribute name is "attribute" and your setter method<br>
     * should like "setAttribute(...)".
     *
     * @author CaiFangwei
     * @time since Dec 4, 2015 3:12:43 PM
     * @param paramList
     * @param paramPO
     * @param oldPO
     * @return
     * @throws Exception
     */
    public static <T> T assignValue(String [] paramList,T paramPO,T oldPO){

        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) oldPO.getClass();

        try{
            Field [] fields = getFields(paramList,clazz);

            Method method = null;
            for(Field field : fields){
                Object value = field.get(paramPO);

                if(value==null) continue;

                String methodName = ReflectUtils.createMethodName(field,false);
                //if(methodName==null) continue;
                method = clazz.getDeclaredMethod(methodName, field.getType());
                if(!Modifier.isPublic(method.getModifiers())) continue;

                method.invoke(oldPO, value);
            }
        }catch(Exception e){
            e.printStackTrace();
            oldPO = null;
        }

        return oldPO;
    }

    /**
     * Assign the value of obejct's attribute into map parameter.
     * @author Fangwei_Cai
     * @time since 2016年4月24日 下午3:56:01
     * @param map
     * @param object
     */
    public static <T> boolean assignValueToMap(Map<String,Object> map, T object){
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        Field [] fields = getFields(null,clazz);
        try{
            for(Field field : fields){
                map.put(field.getName(), field.get(object));
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @author Fangwei_Cai
     * @time since 2016年4月24日 下午4:24:38
     * @param paramList
     * @param clazz
     * @return
     */
    public static <T> Field[] getFields(String [] paramList, Class<T> clazz ){
        Field [] fields = null;
        if(paramList == null){
            fields = clazz.getDeclaredFields();

        }else{
            try{
                // Get the value out from given POJO parameter and store into map collection for further process.
                fields = new Field[paramList.length];
                for(int i=0;i<paramList.length;i++){
                    fields[i] = clazz.getDeclaredField(paramList[i]);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        if(fields != null){
            Field.setAccessible(fields, true);
        }

        return fields;
    }

    /**
     * @author Fangwei_Cai
     * @time since 2016年4月24日 下午4:24:49
     * @param simpleIdentiferName
     * @return
     */
    @Deprecated
    private static boolean setterIdentiferHas(String simpleIdentiferName){

        return ReflectConsts.generalIdentifierSimpleNamesContains(simpleIdentiferName);

    }

    /**
     * @author Fangwei_Cai
     * @create 2016年7月11日14:36:59
     * @param isIdentifierName
     * @return
     */
    private static boolean isIdentifierHas(String isIdentifierName){
        return ReflectConsts.isIdentifierSimpleNamesContains(isIdentifierName);
    }

    /**
     * Get specified property's value.<br/>
     * If specified property is illegal, null will be returned.<p/>
     *
     * @param object
     * @param propertyName
     * @return
     */
    public static Object getSpecifiedPropertyValue(Object object,String propertyName) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = object.getClass();
        Field field = clazz.getDeclaredField(propertyName);
        field.setAccessible(true);
        String methodName = ReflectUtils.createMethodName(field,true);
        Method method = clazz.getDeclaredMethod(methodName);
        return method.invoke(object);

    }

    /**
     * Invoke all declared method of object and put the value into resultMap.<br/>
     * ResultMap use method's name as key.
     * @author CaiFangwei
     * @time since 2016-11-25 09:34:50
     * @param object
     * @param resultMap
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void putMethodResultIntoMap(Object object,Map<String,Object> resultMap) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class clazz = object.getClass();
        Method [] declaredMethods = clazz.getDeclaredMethods();
        for(Method declaredMethod : declaredMethods){
            Class<?> [] parameterTypes = declaredMethod.getParameterTypes();
            if(parameterTypes.length > 0) continue;
            Method method = clazz.getDeclaredMethod(declaredMethod.getName(),parameterTypes);
            resultMap.put(method.getName(),method.invoke(object));
        }
    }

}
