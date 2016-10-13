package cfw.redis.annotation;

import cfw.redis.util.Direction;
import cfw.redis.util.KeyType;
import cfw.redis.util.ListOrder;
import cfw.redis.util.StoredSetOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Fangwei_Cai
 * @time since 2016年6月24日 上午10:59:00
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {
	
	String key() default "";
	
	KeyType keyType() default KeyType.String;

	Direction direction() default Direction.LEFT;

	ListOrder listOrder() default ListOrder.RANGE;

	StoredSetOrder storedSetOrder() default StoredSetOrder.ZADD;

	int expire() default 0;
	
}
