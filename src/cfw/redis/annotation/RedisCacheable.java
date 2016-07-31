package cfw.redis.annotation;

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
	
	public enum KeyType {
		STRING,
		LIST,
		SET,
		ZSET,
		HASH
	}
	
	String key() default "";
	
	KeyType keyType() default KeyType.STRING;

	public enum Direction {
		RIGHT,LEFT
	}

	Direction direction() default Direction.LEFT;

	public enum ListOrder {
		INDEX,INSERT,LEN,POP,PUSH,RANGE,REM,SET,TRIM
	}

	ListOrder listOrder() default ListOrder.RANGE;


	
}
