package cfw.redis;

import cfw.redis.util.StoredSetOrder;
import cfw.reflect.ReflectUtils;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Duskrain on 2016/10/8.
 */
public class CJedisStoredSet extends BaseJedis {

    public CJedisStoredSet(final JedisPool jedisPool){
        if(jedisPool != null){
            this.jedis = jedisPool.getResource();
        }
    }

    /**
     * @author Fangwei_Cai
     * @time since 2016-10-10 21:15:22
     * @param method
     * @param redisPropertyMap
     * @param key
     * @return
     */
    public Object process(Method method, Map<String,Object> redisPropertyMap, String key,List<?> values) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        StoredSetOrder order = (StoredSetOrder) redisPropertyMap.get("storedSetOrder");

        switch(order){
            case ZADD:
                result = this.zadd(redisPropertyMap,key,values);
                break;
            case ZCARD:
                break;
            case ZCOUNT:
                break;
            case ZINCRBY:
                break;
            case ZINTERSTORE:
                break;
            case ZRANGEBYSCORE:
                break;
            case ZRANK:
                break;
            case ZREM:
                break;
            case ZREMRANGEBYRANK:
                break;
            case ZREMRANGEBYSCORE:
                break;
            case ZREVRANGE:
                break;
            case ZREVRANGEBYSCORE:
                break;
            case ZREVRANK:
                break;
            case ZSCAN:
                break;
            case ZSCORE:
                break;
            case ZUNIONSTORE:
                break;
        }

        return result;
    }

    /**
     * @author Fangwei_Cai
     * @time since 2016-10-13 17:40:08
     * @param redisPropertyMap
     * @param key
     * @param values
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private boolean zadd(Map<String,Object> redisPropertyMap, String key,List<?> values) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        if(values.size() <= 0) return false;
        Class clazz = values.get(0).getClass();
        // get record's id as score and store the value.
        Map<Double,String> map = new TreeMap<Double,String>();
        for(Object value : values){
            Double id = (Double) ReflectUtils.getSpecifiedPropertyValue(value,"id");
            String valueString = this.gson.toJson(value);
            map.put(id,valueString);
        }

        this.jedis.zadd(key,map);
        return false;
    }
}
