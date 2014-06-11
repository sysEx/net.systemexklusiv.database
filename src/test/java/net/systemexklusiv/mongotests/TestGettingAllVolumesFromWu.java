package net.systemexklusiv.mongotests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import junit.framework.TestCase;

public class TestGettingAllVolumesFromWu extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	 /**
     * this tests a query to get specific volume
     */
	DBCollection volColl = null;
	StringBuilder sb = new StringBuilder();
    public void testApp()
    {
    	
        StopWatch clock = new StopWatch();
        List<String> result = new ArrayList<String>();
        String WU_ID_TO_QUERY = "_id";
        try {
        	clock.start();
			MongoClient mongo = new MongoClient("localhost", 27017);
			List<String> dbs = mongo.getDatabaseNames();
			
			DB db = mongo.getDB("arc");
			// setting this first - we need it globally
			volColl = db.getCollection("volume");
			DBCollection wuColl = db.getCollection("wu");
			
			// get an iteratable for all working unit entries
			DBCursor wuCursor = wuColl.find();
			clock.stop();		
			 while(wuCursor.hasNext()) {
				 BasicDBObject workingUnit = (BasicDBObject) wuCursor.next();
				 String currWuIdVal = workingUnit.getString(WU_ID_TO_QUERY);
				 String wuTitle = (String) workingUnit.get("obj_title");
				 sb.append("queried working unit title: " + wuTitle);
				 sb.append("\n");
				 printVolumeDetails(currWuIdVal);
			 }
		    result.add(sb.toString());
			   
			   System.out.println(sb.toString());
			   System.out.println("this query took in seconds: " + clock.getTotalTimeSeconds());
			   
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        assertTrue( !result.isEmpty() );
    }
    
    /**
     * convenience method to print out volumina specifics
     * 
     * @param volRefId
     */
    private void printVolumeDetails(String volRefId) {
		String VOLUME_ID_REF_TO_QUREY = "obj_id";
		
		BasicDBObject volumeQuery = new BasicDBObject(VOLUME_ID_REF_TO_QUREY, volRefId);
		/*
		 * get the respective volume out of the tuples of the volumes coll.
		 */
		DBObject volume = volColl.findOne(volumeQuery);
		
		String VOL_START_KEY_TO_QUERY = "obj_start";
		String VOL_END_KEY_TO_QUERY = "obj_end";
		
		// get the start and end entries from the current object
		long obj_start =  (Long) volume.get(VOL_START_KEY_TO_QUERY);
		long obj_end =  (Long) volume.get(VOL_END_KEY_TO_QUERY);
		
		sb.append("this volume starts @ word index: " + obj_start);
		sb.append("\n");
	    sb.append("this volume ends @ word index: " + obj_end);
	    sb.append("\n");
    }
}
