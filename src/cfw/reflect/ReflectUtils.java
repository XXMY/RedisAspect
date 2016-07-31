package cfw.reflect;

import java.lang.reflect.Method;

/**
 * Created by Cfw on 2016/7/23.
 */
public class ReflectUtils {

    /**
     * Get generic type of specific method's return type in string.
     * <p>
     * If the return type of method is not generic, null will be returned.
     * </p>
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

}
