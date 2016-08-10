package cfw.redis;

import cfw.redis.annotation.RedisCacheable;
import cfw.redis.util.Direction;
import cfw.redis.util.ListOrder;
import cfw.reflect.ReflectUtils;
import com.google.gson.Gson;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Cfw on 2016/7/23.
 */
class CJedisList {

    private Jedis jedis;

    public CJedisList(Jedis jedis){
        if(jedis != null)
            this.jedis = jedis;
    }

    /**
     * @author Fnagwei_Cai
     * @create 2016-7-23 15:41:34
     * @param method
     * @param redisPropertyMap
     * @param key
     * @return
     */
    public Object process(Method method, Map<String,Object> redisPropertyMap,String key,List<?> values){
        Object result = null;
        ListOrder order = (ListOrder) redisPropertyMap.get("listOrder");
        Direction direction = (Direction) redisPropertyMap.get("direction");

        if(order == null || direction == null) return result;

        Class genericTypeClass = null;
        if(method != null){
            String genericTypeName = ReflectUtils.getGenericTypeName(method);
            try {
                genericTypeClass = Class.forName(genericTypeName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        switch(order){
            case INDEX:
                break;
            case INSERT:
                break;
            case LEN:
                break;
            case POP:
                break;
            case PUSH:
                result = this.push(key,values,direction);
                break;
            case RANGE:
                Long start = (Long) redisPropertyMap.get("start");
                Long end = (Long) redisPropertyMap.get("end");
                if(start == null) start = 0L;
                if(end == null) end = -1L;

                result = this.range(key,start,end,genericTypeClass);

                break;
            case REM:
                break;
            case SET:
                break;
            case TRIM:
                break;
        }

        return result;
    }

    /**
     * @author Fangwei_Cai
     * @create 2016-7-23 17:40:56
     * @param key
     * @param values
     * @param direction
     * @return
     */
    private Long push(String key,List<?> values, Direction direction){
        Long pushResult = 0L;

        if(StringUtils.isEmpty(key) || values == null || values.size() == 0) return pushResult;

        Gson gson = new Gson();
        String [] valueArray = new String[values.size()];
        for(int i=0;i<values.size();i++){
            valueArray[i] = gson.toJson(values.get(i));
        }

        if(direction == null) direction = Direction.LEFT;
        switch(direction){
            case LEFT:
                pushResult = this.jedis.lpush(key,valueArray);
                break;
            case RIGHT:
                pushResult = this.jedis.rpush(key,valueArray);
                break;
        }

        return pushResult;
    }

    /**
     * @author Fnagwei_Cai
     * @create 2016-7-23 16:44:08
     * @param key
     * @param start
     * @param end
     * @param classofT
     * @param <T>
     * @return
     */
    private <T> List<T> range(String key,Long start, Long end,Class<T> classofT){

        List<T> returnResult = null;

        List<String> jedisResult;
        try {
             jedisResult = this.jedis.lrange(key,start,end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if(jedisResult == null) return returnResult;

        Gson gson = new Gson();
        returnResult = new ArrayList<>();
        for(String resultString : jedisResult){
            T resultObject = gson.fromJson(resultString,classofT);
            returnResult.add(resultObject);
        }

        return returnResult;

    }


}
