package cfw.redis.exception;

/**
 * CJedis initialize exception, used for failure while
 * initializing CJedis.
 * Created by Cfw on 2016/8/14.
 */
public class CRedisInitializeException extends Exception {

    public CRedisInitializeException(String message){
        super(message);

    }
}
