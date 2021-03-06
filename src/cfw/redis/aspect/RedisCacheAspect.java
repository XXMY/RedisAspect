package cfw.redis.aspect;

import cfw.redis.CJedis;
import cfw.redis.annotation.*;
import cfw.redis.exception.CRedisInitializeException;
import cfw.reflect.SimpleAssign;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fangwei_Cai
 * @time since 2016年6月24日 上午11:32:39
 */
@Aspect
@Component
public class RedisCacheAspect {


	private final String cache = "@annotation(cfw.redis.annotation.RedisCacheable)";

	@Resource(name = "jedisPool")
	private JedisPool jedisPool;

	/**
	 * @author Fangwei_Cai
	 * @time since 2016年6月26日 下午3:12:34
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around(value = cache)
	public Object process(ProceedingJoinPoint pjp) throws Throwable{
		CJedis cJedis = this.initCJedis();
		try{
			Method method = ((MethodSignature) pjp.getSignature()).getMethod();
			Object [] args = pjp.getArgs();

			Map<String,Object> map = this.getAnnotationProperties(method, args);

			Object value = null;
			value = cJedis.getRedisValue(method, map);

			if(value == null){
				value = pjp.proceed();
				if(value != null){
					cJedis.saveRedisValue(map,value);
				}
			}

			return value;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.releaseCJedis(cJedis);
		}

		return null;
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
	public Map<String,Object> getAnnotationProperties(Method method,Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
                    if(an instanceof RedisParam){
                        this.getPropertiesFromBean(buffer,fields,map,args[i]);
                    }else{
                        this.putValuesIntoMap(an,map,fields,buffer,args[i]);

                    }
                }

			}
			if(fields.size()>0) map.put("fields",fields.toArray(new String[]{}));
			map.put("key", buffer.toString());
			map.put("keyType", redisCacheable.keyType());
			map.put("direction",redisCacheable.direction());
			map.put("listOrder",redisCacheable.listOrder());
			map.put("expire",redisCacheable.expire());
		}
		
		return map;
	}

    /**
     * @author Fangwei_Cai
     * @time since 2016-8-21 14:38:43
     * @param bean
     * @return
     */
	private void getPropertiesFromBean(StringBuffer buffer,List<String> redisAnnotationsFields, Map<String,Object> propertyMap,Object bean) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = bean.getClass();

        Field[] fields = clazz.getDeclaredFields();
		Field.setAccessible(fields,true);
		for(Field field : fields){
            Annotation [] annotations = field.getDeclaredAnnotations();

			// Get value of currnet field.
			String getValueMethodName = SimpleAssign.createMethodName(field,true);
			Method getValueMethod = clazz.getDeclaredMethod(getValueMethodName,null);
			Object fieldValue = getValueMethod.invoke(bean);

            for(Annotation annotation : annotations){
                this.putValuesIntoMap(annotation,propertyMap,redisAnnotationsFields,buffer,fieldValue);
            }

        }

	}

    private void putValuesIntoMap(Annotation annotation,Map<String,Object> map,List<String> fields,StringBuffer buffer,Object arg){
        if(annotation instanceof RedisID){
            buffer.append(":");
            buffer.append(arg);
        }else if(annotation instanceof RedisField){
            fields.add(arg.toString());
        }else if(annotation instanceof RedisStart){
            map.put("start", arg);
        }else if(annotation instanceof RedisEnd){
            map.put("end", arg);
        }

    }

	private CJedis initCJedis() throws CRedisInitializeException {
		if(this.jedisPool != null){
			CJedis cJedis = new CJedis(this.jedisPool);

			return cJedis;
		}

		return null;
	}

	private void releaseCJedis(CJedis cJedis){
		if(cJedis != null){
			cJedis.close(this.jedisPool);
		}
	}
	
}
