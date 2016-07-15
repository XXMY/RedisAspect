package test.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Fangwei_Cai
 * @time since 2016年3月26日 上午11:57:31
 */
public class SpringUtil {

	private static ApplicationContext applicationContext;
	
	static {
		applicationContext = new ClassPathXmlApplicationContext("spring/ApplicationContext*.xml");
		
	}
	
	public static Object getBean(String beanName){
		Object object = applicationContext.getBean(beanName);
		
		return object;
	}
}
