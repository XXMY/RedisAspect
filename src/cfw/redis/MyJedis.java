package cfw.redis;

import cfw.redis.annotation.RedisCacheable;
import cfw.reflect.SimpleAssign;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Customize jedis bean, encapsulate methods to simplify redis operation.<br>
 * This bean can be injected into another bean as property.
 * @author Fangwei_Cai
 * @time since 2016年6月23日 下午6:15:39
 */
public class MyJedis {
	
	private Jedis jedis;
	
	public MyJedis(JedisPool jedisPool){
		super();
		if(jedisPool != null){
			this.jedis = jedisPool.getResource();
		}
	}
	

	public String get(String key){
		String value = this.jedis.get(key);
		
		return value;
	}

	/**
	 * @author Fangwei_Cai
	 * @time since 2016年6月26日 下午3:09:53
	 * @param method
	 * @param redisPropertyMap
	 * @return
	 */
	public Object getRedisValue(Method method, Map<String,Object> redisPropertyMap){

		String key = (String)redisPropertyMap.get("key");

		Object result = null;

		if(StringUtils.isNotEmpty(key)){
			RedisCacheable.KeyType keyType = (RedisCacheable.KeyType)redisPropertyMap.get("keyType");
			switch(keyType){
				case STRING:
					result = this.jedis.get(key);
					break;
				case LIST:
					long start = (long) redisPropertyMap.get("start");
					long end = (long) redisPropertyMap.get("end");
					result = this.jedis.lrange(key, start, end);
					break;
				case SET:

					break;
				case HASH:
					/*String field = (String) redisPropertyMap.get("field");
					result = this.jedis.hget(key, field);*/
					String [] fields = (String []) redisPropertyMap.get("fields");
					Map<String,String> hashValue = this.getHashData(key,fields);
					result = this.convertHashToRealType(method.getReturnType().getName(),hashValue);
					break;
				default:
					break;
			}
		}

		String returnTypeName = method.getGenericReturnType().toString();

		if(result == null) return null;
		//Object value = this.convertToRealType(returnTypeName, result);

		return result;
	}

	/**
	 * @author Fangwei_Cai
	 * @time since 2016年7月11日11:33:45
	 * @param redisPropertyMap
	 * @param value
     * @return
     */
	public boolean saveRedisValue(Map<String,Object> redisPropertyMap,Object value){
		String key = (String)redisPropertyMap.get("key");

		if(StringUtils.isNotEmpty(key)) {
			RedisCacheable.KeyType keyType = (RedisCacheable.KeyType) redisPropertyMap.get("keyType");
			switch (keyType){
				case STRING:
					this.jedis.set((String)redisPropertyMap.get("key"), value.toString());
					break;
				case LIST:
					break;
				case SET:
					break;
				case HASH:
					this.saveHashData(key,value);
					break;
				default:
					break;
			}
		}

		return true;
	}

	/**
	 * @author Fangwei_Cai
	 * @create 2016年7月11日14:44:32
	 * @param key
	 * @param value
     * @return
     */
	public boolean saveHashData(String key,Object value){

		Class clazz = value.getClass();
		Field[] fields = SimpleAssign.getFields(null,clazz);
		for(Field field : fields){
			try {
				Object result = field.get(value);
				String hashField = field.getName();
				if(result!=null)
					this.jedis.hset(key,hashField,result.toString());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
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
	 * Convert result from redis into annotated method's return type.<br>
	 * There have many imperfections.
	 * @author Fangwei_Cai
	 * @time since 2016年6月26日 下午3:11:29
	 * @param returnTypeName  method return type in string
	 * @param result redis data result
	 * @return Object
	 */
	private Object convertToRealType(String returnTypeName,Object result){

		String resultTypeName = result.getClass().getName();
		// While return type equals result data type, return result directly.
		// Generally is String type.
		if(returnTypeName.equalsIgnoreCase(resultTypeName))
			return result;

		String parentTypeName = "";
		String sonTypeName = "";
		if(returnTypeName.matches(".*<.*>")){
			parentTypeName = returnTypeName.substring(0, returnTypeName.indexOf("<"));
			sonTypeName = returnTypeName.substring(returnTypeName.indexOf("<")+1, returnTypeName.indexOf(">"));
		}else{
			// Simple Object.
			parentTypeName = returnTypeName;
			sonTypeName = null;
		}

		return null;
	}

	/**
	 * @author Fangwei_Cai
	 * @create 2016年7月12日11:29:36
	 * @param method
	 * @param result
     * @return
     */
	private Object convertToRealType(Method method, Map<String,String> result){
		String returnTypeName = method.getGenericReturnType().toString();

		if(result == null) return null;
		Object value = this.convertToRealType(returnTypeName, result);
		return null;
	}

	/**
	 * Convert redis hash data to Object.
	 * @param returnTypeName
	 * @param result
     * @return
     */
	private Object convertHashToRealType(String returnTypeName,Map<String,String> result){

		if(StringUtils.isEmpty(returnTypeName) || result == null) return null;

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
				Object entryValueObejct = entryValue;
				if(entryValue.startsWith("REDIS_HASH_KEY.")){
					String propertyRedisKey = entryValue.split("REDIS_HASH_KEY.")[1];
					Map<String,String> hashData = this.getHashData(propertyRedisKey,null);
					Field sonPropertyField = returnClass.getDeclaredField(name);
					String sonPropertyClassTypeName = sonPropertyField.getType().getName();
					// Recursive.
					entryValueObejct = this.convertHashToRealType(sonPropertyClassTypeName,hashData);
				}
				// Perform the assignment for this property
				beanUtilsBean.setProperty(returnObject, name, entryValueObejct);

			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return returnObject;
	}
}
