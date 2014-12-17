package recommender.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import recommender.core.FBConcertsRecommender;

public class TestFileModel implements TestModelInterface {
	FBConcertsRecommender fileRecommender;

	public TestFileModel() {
		fileRecommender = new FBConcertsRecommender();
	}

	public static void main(String[] args) {
		TestFileModel fileModelTest = new TestFileModel();
		String testJSON = "{\n"												+
							  "\"type\": 1,\n"								+
							  "\"id\" : \"685885852_572\",\n"				+
							  "\"likes\":[\"3333\",\"55555\",\"7777\"]\n"	+
						   "}";
		
		System.out.println(testJSON);
		fileModelTest.testReadingJSON(testJSON);
		fileModelTest.testWritingJSON();
		System.out.println("______________\n");
		
		Long userID = new Long(2);
		String recommendations = fileModelTest.recommend(testJSON);
		System.out.println(recommendations);
		userID = new Long(4);
		recommendations = fileModelTest.recommend(testJSON);
		System.out.println(recommendations);
		
	}

	@Override
	public String recommend(String json) {
		return this.fileRecommender.recommend(json);
	}
	
	public void testReadingJSON(String json) {
		JsonNode 	  	 rootNode, type, userNode, arrayNode;
		ObjectMapper 	 jsonObj	= new ObjectMapper();
		List<Preference> userPrefs	= new ArrayList<Preference>();
		String localID;
		try {
			rootNode = jsonObj.readTree(json);
			type = rootNode.path("type");
			
			System.out.println("type: " + type);
			userNode = rootNode.path("id");
			localID  = userNode.asText();
			System.out.println("local Id " + localID);
			arrayNode = rootNode.path("likes");
			for (JsonNode itemNode : arrayNode) {
				System.out.println("item node " + itemNode.asText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void testWritingJSON() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator;
		String json = "";
		try {
			jsonGenerator = jsonFactory.createJsonGenerator(outputStream);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("id", "" + "USER_ID");
			jsonGenerator.writeFieldName("recommended");
			jsonGenerator.writeStartArray();
			
			for (int i = 111; i<120; i++) {
				jsonGenerator.writeString("" + i);
				System.out.println("Recommended for user : " + i);
			}
			jsonGenerator.writeEndArray();
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			
			json = outputStream.toString();
			System.out.println("json: " +json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
