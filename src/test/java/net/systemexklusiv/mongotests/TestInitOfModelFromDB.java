package net.systemexklusiv.mongotests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.systemexklusiv.mongotests.utils.ArcMongoHelper;

import org.bson.BasicBSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.spinfo.arc.editor.shared.model3.modifiable.ModifiableWithParent;
import de.spinfo.arc.editor.shared.model3.modification.payload.RangeUnit;
import de.spinfo.arc.editor.shared.model3.modification.payload.UsableGwtRectangle;
import de.spinfo.arc.editor.shared.model3.modification.payload.impl.UsableGwtRectangleImpl;
import de.spinfo.arc.editor.shared.model3.util.ModelConstants.MODIFICATION.Types;
import de.spinfo.arc.editor.shared.model3.util.factory.WorkingUnitFactory;
import de.spinfo.arc.editor.shared.model3.util.factory.impl.WorkingUnitFactoryImpl;
import de.spinfo.arc.editor.shared.model3.workingunit.WorkingUnit;
import junit.framework.TestCase;

public class TestInitOfModelFromDB extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	  /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	
        StopWatch clock = new StopWatch();
        List<String> result = new ArrayList<String>();
        /*
         * In order to get the volume which is encapsulated by an Wu-collection
         * the "_id" is a reference to the volume's collections "obj_id"
         */
        String WU_ID_TO_QUERY = "_id";
        String WU_VAL_TO_QUERY = "oct_001";	
        
        WorkingUnitFactory FAC = WorkingUnitFactoryImpl.INSTANCE;
        WorkingUnit workingUnit;
        try {
        	clock.start();
			MongoClient mongo = new MongoClient("localhost", 27017);
			List<String> dbs = mongo.getDatabaseNames();
			
			DB db = mongo.getDB("arc");
			DBCollection wuColl = db.getCollection("wu");
			
			/*
			 * get the DBObjects of the workingunit collections in order to get
			 * the reference to the volumes. 
			 */
			BasicDBObject wuQuery = new BasicDBObject(WU_ID_TO_QUERY, WU_VAL_TO_QUERY);
			DBObject workingUnitObj = wuColl.findOne(wuQuery);
			
			String wuTitle = (String) workingUnitObj.get("obj_title");
			long milis = (Long) workingUnitObj.get("date");
			Date date = new Date(milis);
			
			
			
			workingUnit = FAC.createWorkingUnit(FAC.createDescription(0,
					wuTitle), date, -1);
			
			/*
			 * conntect to different Collection to query a volume
			 */
			DBCollection volColl = db.getCollection("volume");
			/*
			 * get the respective volume out of the tuples of the working unit db object 
			 */
			// get the ref id out of the wu collections
			String obj_id = (String) workingUnitObj.get(WU_ID_TO_QUERY);
			// this is the specific key the workin unit entry is refereing to
			String VOLUME_ID_REF_TO_QUREY = "obj_id";
			
			BasicDBObject volumeQuery = new BasicDBObject(VOLUME_ID_REF_TO_QUREY, obj_id);
			DBObject volume = volColl.findOne(volumeQuery);
			
			String VOL_START_KEY_TO_QUERY = "obj_start";
			String VOL_END_KEY_TO_QUERY = "obj_end";
			
			long obj_start =  (Long) volume.get(VOL_START_KEY_TO_QUERY);
			long obj_end =  (Long) volume.get(VOL_END_KEY_TO_QUERY);
			
			/*
			 * now, based on the start and end of a volume we find out if the page ranges are contained
			 * therefore we have to get a subset of the page collection
			 */
			DBCollection pageColl = db.getCollection("page");
			// defining a "greater than or equals" start 
			BasicDBObject pageQuery = new BasicDBObject(
					"obj_start", new BasicDBObject( "$gte", obj_start )
			)// defining a "lower than or equals" end 
			.append("obj_end", new BasicDBObject( "$lte", obj_end ));
			
			DBCursor pagesOfVolumeCursor = pageColl.find(pageQuery);
			
			
			StringBuilder sb = new StringBuilder();
			// we need to have acces while iterating later
			DBCollection wordColl = db.getCollection("word"); 
			/*
			 * the last page read in the loop will be put here
			 * this will be needed further to get the words out of it
			 */
			BasicDBObject selectedPageObj = null;
			// the page which should be picked out of the results
			int PAGE_NUM_TO_INSPECT = 0;
			int pageIdx = 0;
			   while(pagesOfVolumeCursor.hasNext()) {
				    BasicDBObject obj = (BasicDBObject) pagesOfVolumeCursor.next();
				    if (pageIdx == PAGE_NUM_TO_INSPECT)
				    	selectedPageObj = obj;
				    result.add(obj.toString());
//				    sb.append(obj.toString());
//				    sb.append("\n");
				    long start = obj.getLong("obj_start");
				    long end = obj.getLong("obj_end");
					RangeUnit ru = FAC.createRange((int) start, (int) end);
					FAC.createAndAppendPageRangeModification( ru , workingUnit, FAC.createDescription(pageIdx, "page"), date, -1);
				    pageIdx++;
				    
				    long wordOnAPageStart =  (Long) obj.get("obj_start");
				    long wordOnAPageEnd =  (Long) obj.get("obj_end");
				    
				    DBCursor  wordsOfSelectedPageCursor = ArcMongoHelper.getWordsInRange(wordOnAPageStart, wordOnAPageEnd, db, "word");
				    
					while(wordsOfSelectedPageCursor.hasNext()) {
						    BasicDBObject wordObj = (BasicDBObject) wordsOfSelectedPageCursor.next();
						    BasicBSONObject rect = (BasicBSONObject) wordObj.get("rect");
						    int width = (Integer) rect.get("width");
				    		int height = (Integer) rect.get("heigth");
				    		int x = (Integer) rect.get("x");
						    int y = (Integer) rect.get("y");
						    UsableGwtRectangle wordRect = new UsableGwtRectangleImpl(x, y, width, height);
						    
						    BasicDBList mods = (BasicDBList) wordObj.get("modifications");
						    BasicDBObject lastMod = (BasicDBObject) mods.get(mods.size()-1);
//						    System.out.println(lastMod);
						    String wordText = lastMod.getString("form");
						    Date formDate = new Date(lastMod.getLong("date"));
						    
							ModifiableWithParent word = FAC
									.createAndAppendWordToWorkingUnit(
											wordText, wordRect, workingUnit, formDate,
											-1);
					   }
					break;
			   }
			    sb.append("specific selected page as string: " +  selectedPageObj.toString());
			    sb.append("\n");
			    clock.stop();
				/*
				 * Now we take a subset, given by the range defined in the page, 
				 * from the words which encompass the words on this page
				 */
			   
				
//				DBCursor  wordsOfSelectedPageCursor = ArcMongoHelper.getWordsInRange(wordOnAPageStart, wordOnAPageEnd, db, "word");
//				
//				   while(wordsOfSelectedPageCursor.hasNext()) {
//					    BasicDBObject obj = (BasicDBObject) wordsOfSelectedPageCursor.next();
//					    sb.append(obj.toString());
//					    sb.append("\n");
//				   }
					
			   sb.append("queried working unit title: " + wuTitle);
			   sb.append("\n");
			   sb.append("is consisting of a volume with obj_id: " + obj_id);
			   sb.append("\n");
			   sb.append("this volume starts @ word index: " + obj_start);
			   sb.append("\n");
			   sb.append("this volume ends @ word index: " + obj_end);
			   sb.append("\n");
			   sb.append("it consists of pages: " + result.size());
			   sb.append("\n");
			   System.out.println(sb.toString());
			   System.out.println(workingUnit.toString());
			   System.out.println("this query took in seconds: " + clock.getTotalTimeSeconds());
			   
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        assertTrue( !result.isEmpty() );
    }
}
