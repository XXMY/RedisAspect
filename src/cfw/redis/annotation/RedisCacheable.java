package cfw.redis.annotation;

import cfw.redis.util.Direction;
import cfw.redis.util.KeyType;
import cfw.redis.util.ListOrder;

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
	
	KeyType keyType() default KeyType.STRING;

	Direction direction() default Direction.LEFT;

	ListOrder listOrder() default ListOrder.RANGE;

	int expire() default 0;
	
}
