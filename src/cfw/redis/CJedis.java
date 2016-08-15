package cfw.redis;

import cfw.redis.exception.CRedisInitializeException;
import cfw.redis.util.KeyType;
import cfw.redis.util.ListOrder;
import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

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
public class CJedis extends BaseJedis{

    private Map<String,BaseJedis> cJedisImpls = new HashMap<>();

	public CJedis(JedisPool jedisPool) throws CRedisInitializeException {
		super();
		if(jedisPool != null){
            this.initialize(jedisPool);
            this.cJedisImpls.put("hash",new CJedisHash(jedisPool));
            this.cJedisImpls.put("list",new CJedisList(jedisPool));
		}
	}

    /**
     * Empty CJedis, set jedis to null.
     * @author Fangwei_Cai
     * @time since 2016-8-14 08:55:11
     * @return
     */
    public boolean close(JedisPool jedisPool){
        if(jedisPool != null){
            this.release(jedisPool);
        }
        Set<String> keys = this.cJedisImpls.keySet();
        for(String key : keys){
            this.cJedisImpls.get(key).release(jedisPool);
        }
        return true;
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
			KeyType keyType = (KeyType)redisPropertyMap.get("keyType");
			switch(keyType){
				case STRING:
					result = this.jedis.get(key);
					break;
				case LIST:
                    CJedisList cJedisList = (CJedisList)this.cJedisImpls.get("list");
					result = cJedisList.process(method,redisPropertyMap,key,null);
					break;
				case SET:

					break;
				case HASH:
                    CJedisHash cJedisHash = (CJedisHash) this.cJedisImpls.get("hash");
					result = cJedisHash.getValue(method,redisPropertyMap,key);
					break;
				default:
					break;
			}
		}

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
			switch (keyType){
				case STRING:
					this.jedis.set((String)redisPropertyMap.get("key"), value.toString());
					break;
				case LIST:
                    redisPropertyMap.remove("listOrder");
                    redisPropertyMap.put("listOrder", ListOrder.PUSH);
                    CJedisList cJedisList = (CJedisList)this.cJedisImpls.get("list");
                    cJedisList.process(null,redisPropertyMap,key,(List)value);
					break;
				case SET:
					break;
				case HASH:
                    CJedisHash cJedisHash = (CJedisHash) this.cJedisImpls.get("hash");
					cJedisHash.saveHashData(key,value,expireTime);
					break;
				default:
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		// Set keys expire time.
		if(expireTime > 0) this.jedis.expire(key,expireTime);

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
