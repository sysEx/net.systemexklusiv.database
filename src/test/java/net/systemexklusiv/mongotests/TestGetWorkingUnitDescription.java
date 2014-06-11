package net.systemexklusiv.mongotests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StopWatch;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import junit.framework.TestCase;

public class TestGetWorkingUnitDescription extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testApp() {

		StopWatch clock = new StopWatch();
		List<String> result = new ArrayList<String>();
		try {
			clock.start();
			MongoClient mongo = new MongoClient("localhost", 27017);
			List<String> dbs = mongo.getDatabaseNames();

			DB db = mongo.getDB("arc");
			DBCollection coll = db.getCollection("wu");

			// BasicDBObject doc = new BasicDBObject("username", "dieter")
			// .append("password", "123456");
			// coll.insert(doc);

			DBCursor cursor = coll.find();

			clock.stop();
			StringBuilder sb = new StringBuilder();
			while (cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				System.out.println(obj.toString());
				result.add(obj.toString());
				sb.append("_id: " + obj.getString("_id"));
				sb.append("\n");
				sb.append("obj_title: " + obj.getString("obj_title"));
				sb.append("\n");
				long milis = Long.valueOf(obj.getString("date"));
				Date date = new Date(milis);
				sb.append("date: " + date.toGMTString());
				sb.append("\n");
			}

			System.out.println(sb.toString());
			System.out.println("this query took in seconds: "
					+ clock.getTotalTimeSeconds());

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(!result.isEmpty());
	}
}
