package cfw.redis;

import cfw.reflect.ReflectConsts;
import cfw.reflect.SimpleAssign;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Cfw on 2016/7/23.
 */
class CJedisHash {

    private Jedis jedis;

    private final static String Redis_Hash_Prefix = "REDIS_HASH_Prefix.";

    public CJedisHash(Jedis jedis){
        this.jedis = jedis;
    }


    public Object getValue(Method method,Map<String,Object> redisPropertyMap,String key){
        Object result = null;

        String [] fields = (String []) redisPropertyMap.get("fields");
        Map<String,String> hashValue = this.getHashData(key,fields);
        if(hashValue != null && !hashValue.isEmpty()){
            result = this.convertHashToRealType(method.getReturnType().getName(),hashValue);
        }

        return result;
    }

    /**
     * @author Fangwei_Cai
     * @create 2016年7月12日11:13:35
     * @param key
     * @param fields
     * @return
     */
    public Map<String,String> getHashData(String key, String [] fields){
        if(StringUtils.isEmpty(key)) return null;

        Map<String,String> hashResult = new HashMap<String,String>();

        if(fields != null){
            for(String field : fields){
                String value = this.jedis.hget(key,field);
                hashResult.put(field,value);
            }
        }else{
            hashResult = this.jedis.hgetAll(key);
        }

        return hashResult;
    }

    /**
     * While save data into redis with hash, result object must be a java bean,<br>
     * and this method will save all properties if property's value is not null.<p></p>
     * If result object contains a bean, call this method in recursion to save it.
     * @author Fangwei_Cai
     * @create 2016年7月11日14:44:32
     * @param key
     * @param result
     * @return
     */
    public boolean saveHashData(String key, Object result){

        if(result == null || StringUtils.isEmpty(key)) return false;

        Class clazz = result.getClass();
        Field[] resultClassFields = SimpleAssign.getFields(null,clazz);
        try{
            for(Field resultClassField : resultClassFields){
                Object value = resultClassField.get(result);
                String valueFieldName = resultClassField.getName();
                String valueFieldTypeName = resultClassField.getType().getSimpleName();
                if(value == null ) continue;

                if(ReflectConsts.generalIdentifierSimpleNamesContains(valueFieldTypeName)){
                    // General type value
                    this.jedis.hset(key,valueFieldName,value.toString());
                }else{
                    // Nested java bean.
                    boolean saveResult = this.saveHashData(key+":"+valueFieldName,value);
                    if(saveResult) this.jedis.hset(key,valueFieldName,Redis_Hash_Prefix+key+":"+valueFieldName);
                }

            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Convert redis hash data to Object.
     * @param returnTypeName
     * @param result
     * @return
     */
    public Object convertHashToRealType(String returnTypeName,Map<String,String> result){

        if(StringUtils.isEmpty(returnTypeName) || result == null) return null;

        if(returnTypeName.equalsIgnoreCase("java.lang.String")){
            Set<String> resultKeys = result.keySet();
            String value = "";
            for(String resultKey : resultKeys){
                value = result.get(resultKey);
            }

            return value;

        }

        Class returnClass = null;
        Object returnObject = null;
        try {
            returnClass = Class.forName(returnTypeName);
            returnObject = returnClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(returnObject == null) return null;
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();

        try{
            for(Map.Entry<String, ? extends Object> entry : result.entrySet()) {
                // Identify the property name and value(s) to be assigned
                String name = entry.getKey();
                if (name == null) {
                    continue;
                }

                String entryValue = (String)entry.getValue();
                Object entryObject = entryValue;
                if(entryValue.startsWith(Redis_Hash_Prefix)){
                    // Define a field's value start with 'REDIS_HASH_KEY.' is a nested java bean.
                    String propertyRedisKey = entryValue.split(Redis_Hash_Prefix)[1];
                    Map<String,String> hashData = this.getHashData(propertyRedisKey,null);
                    Field sonPropertyField = returnClass.getDeclaredField(name);
                    String sonPropertyClassTypeName = sonPropertyField.getType().getName();
                    // Recursive.
                    entryObject = this.convertHashToRealType(sonPropertyClassTypeName,hashData);
                }
                // Perform the assignment for this property
                beanUtilsBean.setProperty(returnObject, name, entryObject);

            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

        return returnObject;
    }

}
