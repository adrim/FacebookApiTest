package recommender.core;

import static recommender.utils.Defines.RESOURCE_PREFIX;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import recommender.utils.Defines;

public class FBConcertsRecommender {
	/**
	 * getTourDates; pick closest location
	 * 
	 */
	private String databaseFile		= RESOURCE_PREFIX + "userLikes";
	private String userMappingsFile = RESOURCE_PREFIX + "userMappings";
	private final Integer NO_NEIGHBORS = 1;
	private DataModel dataModel 					= null;
	private FastByIDMap<PreferenceArray> userData 	= null;
	private String file = "";
	private HashMap<String, Long> userMappings = new HashMap<String, Long>();
	private UserSimilarity userSimilarity = null;
	private ItemSimilarity itemSimilarity = null;
	
	public FBConcertsRecommender() {
		this.initRecommender(this.databaseFile);
	}
	/**
	 * @return the databaseFile
	 */
	public String getDatabaseFile() {
		return databaseFile;
	}

	/**
	 * @param databaseFile the databaseFile to set
	 */
	public void setDatabaseFile(String databaseFile) {
		this.databaseFile = databaseFile;
	}

	/**
	 * @return the userMappingsFile
	 */
	public String getUserMappingsFile() {
		return userMappingsFile;
	}

	/**
	 * @param userMappingsFile the userMappingsFile to set
	 */
	public void setUserMappingsFile(String userMappingsFile) {
		this.userMappingsFile = userMappingsFile;
	}

	/**
	 * @return the userSimilarity
	 */
	public UserSimilarity getUserSimilarity() {
		return userSimilarity;
	}

	/**
	 * @param userSimilarity the userSimilarity to set
	 */
	public void setUserSimilarity(UserSimilarity userSimilarity) {
		this.userSimilarity = userSimilarity;
	}

	/**
	 * @return the itemSimilarity
	 */
	public ItemSimilarity getItemSimilarity() {
		return itemSimilarity;
	}

	/**
	 * @param itemSimilarity the itemSimilarity to set
	 */
	public void setItemSimilarity(ItemSimilarity itemSimilarity) {
		this.itemSimilarity = itemSimilarity;
	}
	
	public void getYoutubeLinkInfo() {
	}
	
	public void initRecommenderFromJDBC() {
//		dataModel = new MySQLJDBCDataModel(myDataSource);
	}
	/**
	 * Read the dataModel to be used from a file
	 * @param databaseFile File containing the DataModel
	 */
	public void readModelFromFile(String databaseFile) {
		System.out.println(".............");
		try {
			dataModel = new FileDataModel(new File(databaseFile));
		} catch (IOException e) {
			System.err.println("\n\tFATAL: could not initialize model\n");
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * Read model from a preset file
	 */
	public void readModelFromFile() {
		readModelFromFile(this.databaseFile);
	}
	/**
	 * Read the mapping from Facebook id to local id
	 * Reason for new id: the recommender uses Long IDs, not String
	 */
	public void readUserMappingsFromFile() {
		if (Files.notExists(Paths.get(this.userMappingsFile),
							LinkOption.NOFOLLOW_LINKS)) {
			return;
		}
		
		Scanner sc = new Scanner(this.userMappingsFile);
		while (sc.hasNext()) {
			this.userMappings.put(sc.next(), sc.nextLong());
		}
	}
	/**
	 * Write Data Model to file to be uploaded when the server goes up
	 */
	public void writeModelToFile() {
		try {
			PrintWriter pr = new PrintWriter(this.databaseFile);
			FastByIDMap<PreferenceArray> userData =
					GenericDataModel.toDataMap(this.dataModel);
			for (Entry<Long, PreferenceArray> userPref  : userData.entrySet()) {
				Long id = userPref.getKey();
				PreferenceArray prefs = userPref.getValue();
				for (Preference pref : prefs) {
					System.out.println("to string pref  " + pref.toString());
					pr.println(id + "," + pref.getItemID() + ", " + pref.getValue());
				}
			}
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Write Data Model to file to be uploaded when the server goes up
	 */
	public void writeModelToFile(FastByIDMap<PreferenceArray> userData,
								 String databaseFile) {
		try {
			PrintWriter pr = new PrintWriter(databaseFile);
			for (Entry<Long, PreferenceArray> userPref  : userData.entrySet()) {
				Long id = userPref.getKey();
				PreferenceArray prefs = userPref.getValue();
				for (Preference pref : prefs) {
					System.out.println("to string pref  " + pref.toString());
					pr.println(id + "," + pref.getItemID() + ", " + pref.getValue());
				}
			}
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void writeUserMappingsToFile() {
		try {
			PrintWriter pw = new PrintWriter(userMappingsFile);
			for (Entry<String, Long> mapping : this.userMappings.entrySet()) {
				pw.println(mapping.toString());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Initialize the recommender
	 * @param file
	 */
	public void initRecommender(String file) {
		readUserMappingsFromFile();
		readModelFromFile();
	}
	public void saveState() {
		writeUserMappingsToFile();
		writeModelToFile();
	}
	public void refreshModel() {
		readModelFromFile();
	}
	private Long facebookIDToLocal(String facebookID) {
		return userMappings.get(facebookID);
	}
	public void initRecommender() {
		dataModel = new GenericDataModel(this.userData);
	}
	/**
	 * type:
	 * <ul>
	 * <li> 1 = user preferences</li>
	 * <li> 2 = post? </li>
	 * </ul>
	 * 		
	 * @param userJson
	 * @throws Exception 
	 */
	public void addUserInfo(String userJson) throws Exception {
		JsonNode 	  	 rootNode, type, userNode, arrayNode;
		ObjectMapper 	 jsonObj	= new ObjectMapper();
		List<Preference> userPrefs	= new ArrayList<Preference>();
		Long localID;
		try {
			rootNode = jsonObj.readTree(userJson);
			type = rootNode.path("type");
			
			System.out.println("type: " + type);
			if (type.asInt() != 1) {
				System.err.println("Not supported");
				return;
			}
			userNode = rootNode.path("id");
			localID  = facebookIDToLocal(userNode.asText());
			
			arrayNode = rootNode.path("likes");
			if (this.userMappings.containsKey(userNode.asText())) {
				updateUserInfo(localID, arrayNode);
				return;
			}
			if (!arrayNode.isArray())
				throw new Exception("Null preference array!");
			
			for (JsonNode itemNode : arrayNode) {
				userPrefs.add(new GenericPreference(
									localID, itemNode.asLong(),	(float) 1.0));
			}
			this.userData = new FastByIDMap<PreferenceArray>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void updateUserInfo(Long localID, JsonNode userNode) throws Exception {
		if (!userNode.isArray())
			throw new Exception("Null preference array!");
		for (JsonNode itemNode : userNode) {
			Long itemID = itemNode.asLong();
			if (this.dataModel.getPreferenceValue(localID, itemID) != null) {
				this.dataModel.removePreference(localID, itemID);
				this.dataModel.setPreference(localID, itemID, (float)itemNode.asDouble());
				
				saveState();
			} else {
				FastByIDMap<PreferenceArray> userData = GenericDataModel.toDataMap(this.dataModel);
				GenericUserPreferenceArray 	 prefs 	  = (GenericUserPreferenceArray) userData.get(localID);
				ArrayList<GenericPreference> newPrefs = new ArrayList<GenericPreference>();
				
				for (Iterator<Preference> it = prefs.iterator(); it.hasNext();) {
					newPrefs.add((GenericPreference) it.next());
				}
				newPrefs.add(new GenericPreference(localID, itemID, (float)itemNode.asDouble()));
				writeModelToFile(userData, this.databaseFile);
			}
		}
	}
	
	/**
	 * 
	 * @param fbUserID The userId for which we try to recommend events
	 * @param maxItems Maximum number of recommendations; default is 1
	 * @return ArrayList of maximum <maxItems> containing the names of the
	 * recommended artists encoded as a JSON string
	 */
	public String recommend(Long userID, Integer maxItems) {
		String json = "";
//		UserSimilarity sim;
		ItemSimilarity sim;

		Recommender			  recommender	  = null;
		UserNeighborhood 	  neighborhood	  = null;
		List<RecommendedItem> recommendations = null;
		
		try {
			sim 		 = new LogLikelihoodSimilarity(dataModel);
//			neighborhood = new NearestNUserNeighborhood(NO_NEIGHBORS, sim, dataModel);
			
			recommender = new GenericItemBasedRecommender(dataModel, sim);
//			recommender  = new GenericUserBasedRecommender(dataModel, neighborhood, sim);
			recommendations = recommender.recommend(userID, maxItems);
			
			
			
		} catch (TasteException e1) {
			System.err.println("Recommender system fault!");
			e1.printStackTrace();
			
			System.exit(0);
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator;
		try {
			jsonGenerator = jsonFactory.createJsonGenerator(outputStream);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("id", "" + userID);
			jsonGenerator.writeFieldName("recommended");
			jsonGenerator.writeStartArray();
			
			for (RecommendedItem item : recommendations) {
				jsonGenerator.writeNumber(item.getItemID());
				System.out.println("Recommended for user [" + userID + "]: " + item.getItemID());
			}
			jsonGenerator.writeEndArray();
			jsonGenerator.writeEndObject();
			
			jsonGenerator.close();
			
			json = outputStream.toString();
//			System.out.println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	public String recommend(Long user) {
		return recommend(user, Defines.DEFAULT_MAXIMUM_RECOMMENDATIONS);
	}
}
