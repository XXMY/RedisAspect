package cfw.redis;

import cfw.redis.exception.CRedisInitializeException;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Cfw on 2016/8/14.
 */
public abstract class BaseJedis {

    protected Jedis jedis;

    protected Gson gson = new Gson();

    protected boolean initialize(final JedisPool jedisPool) throws CRedisInitializeException {
        if(jedisPool == null){
            throw new CRedisInitializeException("jedisPool is null");
        }
        this.jedis = jedisPool.getResource();

        return true;
    }

    protected boolean release(final JedisPool jedisPool){
        if(this.jedis != null && jedisPool != null){
            jedisPool.returnResource(this.jedis);
        }

        this.jedis = null;
        return true;
    }

}
