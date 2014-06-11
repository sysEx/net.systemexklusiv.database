package net.systemexklusiv.mongotests;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TestBasicSpringContainer extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	 public void testApp()
	    {
	    
	        Resource resource = new ClassPathResource("spring.xml");
	        BeanFactory factory = new XmlBeanFactory(resource);
	        
	        Triangle triangle = (Triangle) factory.getBean("triangle");
	        
	        assertNotNull(triangle);
	    }
}
