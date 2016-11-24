package cfw.redis;

import cfw.redis.exception.CRedisInitializeException;
import cfw.redis.util.KeyType;
import cfw.redis.util.ListOrder;
import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Customize jedis bean, encapsulate methods to simplify redis operation.<br>
 * This bean can be injected into another bean as property.
 * @author Fangwei_Cai
 * @time since 2016年6月23日 下午6:15:39
 */
public class CJedis {

	private JedisPool jedisPool;

	public CJedis(JedisPool jedisPool) throws CRedisInitializeException {
		if(jedisPool != null){
            this.jedisPool = jedisPool;
		}
	}


	/**
	 * @author Fangwei_Cai
	 * @time since 2016年6月26日 下午3:09:53
	 * @param method
	 * @param redisPropertyMap
	 * @return
	 */
	public Object getRedisValue(Method method, Map<String,Object> redisPropertyMap) throws Exception {

		String key = (String)redisPropertyMap.get("key");

		Object result = null;
		Jedis jedis = this.jedisPool.getResource();
		if(StringUtils.isNotEmpty(key)){
			KeyType keyType = (KeyType)redisPropertyMap.get("keyType");
			switch(keyType){
				case String:
					result = jedis.get(key);
					break;
				case List:
                    CJedisList cJedisList = new CJedisList(jedis);
					result = cJedisList.process(method,redisPropertyMap,key,null);
					break;
				case Set:
					break;
				case StoredSet:
					CJedisStoredSet cJedisStoredSet = new CJedisStoredSet(jedis);
					result = cJedisStoredSet.process(method,redisPropertyMap,key,null);
					break;
				case Hash:
                    CJedisHash cJedisHash = new CJedisHash(jedis);
					result = cJedisHash.getValue(method,redisPropertyMap,key);
					break;
				default:
					break;
			}
		}

		this.jedisPool.returnResource(jedis);

		if(result == null) return null;

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
        int expireTime = (Integer) redisPropertyMap.get("expire");

		if(StringUtils.isEmpty(key)) return false;

		try{
			KeyType keyType = (KeyType) redisPropertyMap.get("keyType");
			Jedis jedis = this.jedisPool.getResource();
			switch (keyType){
				case String:
					jedis.set((String)redisPropertyMap.get("key"), value.toString());
					break;
				case List:
                    redisPropertyMap.remove("listOrder");
                    redisPropertyMap.put("listOrder", ListOrder.PUSH);
                    CJedisList cJedisList = new CJedisList(jedis);
                    cJedisList.process(null,redisPropertyMap,key,(List)value);
					break;
				case Set:
					break;
				case Hash:
                    CJedisHash cJedisHash = new CJedisHash(jedis);
					cJedisHash.saveHashData(key,value,expireTime);
					break;
				default:
					break;
			}

			// Set keys expire time.
			if(expireTime > 0) jedis.expire(key,expireTime);

			this.jedisPool.returnResource(jedis);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}



		return true;
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

}
