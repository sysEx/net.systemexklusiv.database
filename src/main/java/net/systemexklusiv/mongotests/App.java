package net.systemexklusiv.mongotests;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!");
        Resource resource = new ClassPathResource("spring.xml");
        BeanFactory factory = new XmlBeanFactory(resource);
        
        Triangle triangle = (Triangle) factory.getBean("triangle");
        
        triangle.draw();
        
        try {
			MongoClient mongo = new MongoClient("localhost", 27017);
			List<String> dbs = mongo.getDatabaseNames();
			
			DB db = mongo.getDB("testdb");
			DBCollection coll = db.getCollection("users");
			
			BasicDBObject doc = new BasicDBObject("username", "dieter")
	        .append("password", "123456");
			coll.insert(doc);
			
			DBCursor cursor = coll.find();
			
			   while(cursor.hasNext()) {
			       System.out.println(cursor.next());
			   }

			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
