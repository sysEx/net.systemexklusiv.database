package net.systemexklusiv.mongotests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TestGettingAllWords 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestGettingAllWords( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestGettingAllWords.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	
     
        StopWatch clock = new StopWatch();
        List<String> result = new ArrayList<String>();
        try {
        	clock.start();
			MongoClient mongo = new MongoClient("localhost", 27017);
			List<String> dbs = mongo.getDatabaseNames();
			
			DB db = mongo.getDB("arc");
			DBCollection coll = db.getCollection("word");
			
			
			DBCursor cursor = coll.find();
			
			clock.stop();
//			StringBuilder sb = new StringBuilder();
			   while(cursor.hasNext()) {
				    BasicDBObject obj = (BasicDBObject) cursor.next();
				    System.out.println(obj.toString());
				    result.add(obj.toString());
//				    sb.append("_id: " + obj.getString("_id"));
//				    sb.append("\n");
//				    sb.append("obj_title: " + obj.getString("obj_title"));
//				    sb.append("\n");
//				    long milis = Long.valueOf(obj.getString("date"));
//				    Date date = new Date(milis);
//				    sb.append("date: " +date.toGMTString());
//				    sb.append("\n");
			   }
//			   
//			   System.out.println(sb.toString());
			   System.out.println("Amount word tuples in Collection: " + result.size());
			   System.out.println("this query took in seconds: " + clock.getTotalTimeSeconds());
			   
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        assertTrue( !result.isEmpty() );
    }
}
