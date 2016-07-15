package cfw.redis.aspect;

import cfw.redis.MyJedis;
import cfw.redis.annotation.*;
import com.mchange.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fangwei_Cai
 * @time since 2016年6月24日 上午11:32:39
 */
@Aspect
@Component
public class RedisCacheAspect {


	private final String cache = "@annotation(cfw.redis.annotation.RedisCacheable)";
	
	@Resource(name="myJedis")
	private MyJedis myJedis;

	public MyJedis getMyJedis() {
		return myJedis;
	}

	public void setMyJedis(MyJedis myJedis) {
		this.myJedis = myJedis;
	}

	/**
	 * @author Fangwei_Cai
	 * @time since 2016年6月26日 下午3:12:34
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around(value = cache)
	public Object process(ProceedingJoinPoint pjp) throws Throwable{
		
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		Object [] args = pjp.getArgs();
		
		Map<String,Object> map = this.getAnnotationProperties(method, args);

		Object value = null;
		value = this.myJedis.getRedisValue(method, map);
		
		if(value == null){
			value = pjp.proceed();
			if(value != null){
				this.myJedis.saveRedisValue(map,value);
			}
		}
		
		return value;
	}
	
	/**
	 * Get property map that annotated on method and parameters.
	 * @author Fangwei_Cai
	 * @time since 2016年6月26日 下午3:12:39
	 * @param method
	 * @param args
	 * @return Map<br>
	 *     Keys: key, keyType, field, start ,end
	 */
	private Map<String,Object> getAnnotationProperties(Method method,Object[] args){
		StringBuffer buffer = new StringBuffer();
		Map<String,Object> map = new HashMap<String,Object>();
		
		RedisCacheable redisCacheable = method.getAnnotation(RedisCacheable.class);
		// Method is annotated by RedisCacheable annotation.
		if(redisCacheable != null){
			buffer.append(redisCacheable.key());
			
			Annotation [][] annotation = method.getParameterAnnotations();
			ArrayList<String> fields = new ArrayList<String>();
			for(int i=0;i<args.length;i++){
				for(Annotation an : annotation[i]){
					if(an instanceof RedisID){
						buffer.append(":");
						buffer.append(args[i]);
					}else if(an instanceof RedisField){
                            fields.add(args[i].toString());
					}else if(an instanceof RedisStart){
						map.put("start", args[i]);
					}else if(an instanceof RedisEnd){
						map.put("end", args[i]);
					}
					
				}
			}
			if(fields.size()>0) map.put("fields",fields.toArray(new String[]{}));
			map.put("key", buffer.toString());
			map.put("keyType", redisCacheable.keyType());
		}
		
		return map;
	}

	
}
