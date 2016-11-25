package cfw.redis;

import cfw.redis.exception.CRedisInitializeException;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by Cfw on 2016/8/14.
 */
public abstract class BaseJedis {

    protected Jedis jedis;

    protected Gson gson = new Gson();

    public Object process(Method method, Map<String,Object> redisPropertyMap, String key, List<?> values) throws Exception{
        return null;
    }

}
