package recommender.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

public class YouTubeVideo {
	private String vID   = null;
	private String title = null;
	private final static String TITLE_VALUE = "title";
	private final static String YOUTUBE_GET_LINK  = "https://gdata.youtube.com/feeds/api/videos/";
	private final static String YOUTUBE_RESP_TYPE = "?v=2&alt=json";
	
	public YouTubeVideo(String json) {
		title = getValueFromJson(json, TITLE_VALUE);
		
	}
	public YouTubeVideo(String vID, String title) {
		this.vID = vID;
		this.title = title;
	}
	public void setLink(String link) {
		
	}
	
	
	public static String getValueFromJson(String json, String value) {
		ObjectMapper jsonObj = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = jsonObj.readTree(json);
			JsonNode node = rootNode.path(value);
			JsonNode tnode = node.path("$t");
			return tnode.asText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
