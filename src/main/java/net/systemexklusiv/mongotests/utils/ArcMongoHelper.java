package net.systemexklusiv.mongotests.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class ArcMongoHelper {
	private ArcMongoHelper() {};
	private static final String NAME_OF_WORD_COLL = "word";
	private static final String NAME_OF_PAGE_COLL = "page";
	private static final String NAME_OF_CHAPTER_COLL = "chapter";
	private static final String NAME_OF_LANGUAGE_COLL = "language";
	
	public static DBCursor getWordsInRange(long start, long end, DB arcDb, String collName) {
		StringBuilder sb = new StringBuilder();
		DBCollection wordColl = arcDb.getCollection(collName); 
		
		// defining a "greater than or equals" start 
		BasicDBObject wordQuery = new BasicDBObject(
				"word_index", new BasicDBObject( "$gte", start )
				)// defining a "lower than or equals" end 
		.append("word_index", new BasicDBObject( "$lte", end ));
		
		DBCursor wordsOfSelectedPageCursor = wordColl.find(wordQuery);
		return wordsOfSelectedPageCursor;
	}
	
}
