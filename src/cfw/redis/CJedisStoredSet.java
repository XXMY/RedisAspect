package cfw.redis;

import cfw.redis.util.StoredSetOrder;
import cfw.reflect.ReflectUtils;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
    public Object process(Method method, Map<String,Object> redisPropertyMap, String key,List<?> values) throws Exception {
        Object result = null;
        StoredSetOrder order = (StoredSetOrder) redisPropertyMap.get("storedSetOrder");

        Class genericTypeClass = null;
        if(method != null){
           genericTypeClass = ReflectUtils.getGenericType(method);
        }
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
            case ZRANGE:
                Long start = (Long) redisPropertyMap.get("start");
                Long end = (Long) redisPropertyMap.get("end");
                result = this.zrange(key,start,end,genericTypeClass);
                break;
            case ZRANGEBYSCORE:
                Double min = (Double) redisPropertyMap.get("min");
                Double max = (Double) redisPropertyMap.get("max");
                result = this.zrangeByScore(key,min,max,genericTypeClass);
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
     * Untested.
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
    private Long zadd(Map<String,Object> redisPropertyMap, String key,List<?> values) throws Exception {
        if(values.size() <= 0) return 0L;
        Class clazz = values.get(0).getClass();
        // get record's id as score and store the value.
        Map<Double,String> map = new TreeMap<Double,String>();
        for(Object value : values){
            Double id = (Double) ReflectUtils.getSpecifiedPropertyValue(value,"id");
            String valueString = this.gson.toJson(value);
            map.put(id,valueString);
        }

        return this.jedis.zadd(key,map);
    }

    /**
     * @author Fangwei_Cai
     * @time since 2016-10-14 17:40:19
     * @param key
     * @param T
     * @param <T>
     * @return
     */
    private <T> List<T> zrangeByScore(String key,double min,double max, Class<T> T){
        List<T> returnResult = null;
        Set<String> jedisResults = new TreeSet<String>();
        try{
            jedisResults = this.jedis.zrangeByScore(key,min,max);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(jedisResults == null || jedisResults.size() == 0) return returnResult;

        returnResult = new ArrayList<T>();
        for(String jedisResult : jedisResults){
            T t = this.gson.fromJson(jedisResult,T);
            returnResult.add(t);
        }

        return returnResult;
    }

    /**
     * @author Fangwei_Cai
     * @time since 2016-10-14 17:43:49
     * @param key
     * @param start
     * @param end
     * @param T
     * @param <T>
     * @return
     */
    private <T> List<T> zrange(String key,Long start, Long end, Class<T> T){
        List<T> returnResult = null;
        Set<String> jedisResults = new TreeSet<String>();
        try{
            jedisResults = this.jedis.zrange(key,start,end);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(jedisResults == null || jedisResults.size() == 0) return returnResult;

        returnResult = new ArrayList<T>();
        for(String jedisResult : jedisResults){
            T t = this.gson.fromJson(jedisResult,T);
            returnResult.add(t);
        }
        return returnResult;
    }

}
