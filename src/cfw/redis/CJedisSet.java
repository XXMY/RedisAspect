package cfw.redis;

import cfw.redis.util.Direction;
import cfw.redis.util.ListOrder;
import cfw.reflect.ReflectUtils;
import com.google.gson.Gson;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Cfw on 2016/10/08.
 */
class CJedisSet extends BaseJedis{

    public CJedisSet(final JedisPool jedisPool){
        if(jedisPool != null){
            this.jedis = jedisPool.getResource();
        }
    }



}
