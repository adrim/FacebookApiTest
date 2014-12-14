package fb;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.Url;
import com.restfb.types.User;


public class Main {
	private FacebookClient fbClient = null;
	private FacebookClient publicFbClient = null;
	private final String MY_APP_SECRET = null;
//	String accessToken = null;
	String accessToken = "769034396515940";
//	PrintWriter out = new PrintWriter(System.out);
	PrintStream out = System.out;
	
	Main(String accessToken) {
//		fbClient = new DefaultFacebookClient(accessToken, MY_APP_SECRET);
		this.accessToken = accessToken;
		
		if (this.accessToken != null)
			fbClient = new DefaultFacebookClient(accessToken);
		else 
			fbClient = new DefaultFacebookClient();
		publicFbClient = new DefaultFacebookClient();
	}
	
	Main() {
//		fbClient = new DefaultFacebookClient(accessToken, MY_APP_SECRET);
		if (accessToken != null)
			fbClient = new DefaultFacebookClient(accessToken);
		else 
			fbClient = new DefaultFacebookClient();
		publicFbClient = new DefaultFacebookClient();
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.runEverything();
	}
	
	void runEverything() {
	    fetchObject();
	    fetchConnections();
	    /*fetchObjects();
	    fetchObjectsAsJsonObject();
	    fetchConnections();
	    fetchDifferentDataTypesAsJsonObject();
	    query();
	    multiquery();
	    search();
	    metadata();
	    paging();
	    selection();
	    parameters();
	    rawJsonResponse();*/
	}
	void runDefault() {
		
	}
	
	  void fetchObject() {
	    out.println("* Fetching single objects *");

	    User user = fbClient.fetchObject("mihaelaalexandra.neagu", User.class);
	    Page page = fbClient.fetchObject("cocacola", Page.class);

	    out.println("User name: " + user.getName());
	    out.println("Page likes: " + page.getLikes());
	  }

	  void fetchObjectsAsJsonObject() {
	    out.println("* Fetching multiple objects at once as a JsonObject *");

	    List<String> ids = new ArrayList<String>();
	    ids.add("btaylor");
	    ids.add("http://www.imdb.com/title/tt0117500/");

	    // Make the API call
	    JsonObject results = fbClient.fetchObjects(ids, JsonObject.class);

	    // Pull out JSON data by key and map each type by hand.
	    JsonMapper jsonMapper = new DefaultJsonMapper();
	    User user = jsonMapper.toJavaObject(results.getString("btaylor"), User.class);
	    Url url = jsonMapper.toJavaObject(results.getString("http://www.imdb.com/title/tt0117500/"), Url.class);

	    out.println("User is " + user);
	    out.println("URL is " + url);
	  }

	  void fetchObjects() {
	    out.println("* Fetching multiple objects at once *");

	    FetchObjectsResults fetchObjectsResults =
	        fbClient.fetchObjects(Arrays.asList("me", "cocacola"), FetchObjectsResults.class);

	    out.println("User name: " + fetchObjectsResults.me.getName());
	    out.println("Page likes: " + fetchObjectsResults.page.getLikes());
	  }

	  void fetchDifferentDataTypesAsJsonObject() {
	    out.println("* Fetching different types of data as JsonObject *");

	    JsonObject btaylor = fbClient.fetchObject("btaylor", JsonObject.class);
	    out.println(btaylor.getString("name"));

	    JsonObject photosConnection = fbClient.fetchObject("me/photos", JsonObject.class);
	    JsonArray photosConnectionData = photosConnection.getJsonArray("data");

	    if (photosConnectionData.length() > 0) {
	      String firstPhotoUrl = photosConnectionData.getJsonObject(0).getString("source");
	      out.println(firstPhotoUrl);
	    }

	    String query = "SELECT uid, name FROM user WHERE uid=220439 or uid=7901103";
	    List<JsonObject> queryResults = fbClient.executeQuery(query, JsonObject.class);

	    if (queryResults.size() > 0)
	      out.println(queryResults.get(0).getString("name"));
	  }

	  /**
	   * Holds results from a "fetchObjects" call.
	   */
	  public static class FetchObjectsResults {
	    @Facebook
	    User me;

	    @Facebook(value = "cocacola")
	    Page page;
	  }

	  void fetchConnections() {
	    out.println("* Fetching connections *");

	    Connection<User> myFriends = fbClient.fetchConnection("me/friends", User.class);
	    Connection<Post> myFeed = fbClient.fetchConnection("me/feed", Post.class);

	    out.println("Count of my friends: " + myFriends.getData().size());

	    if (myFeed.getData().size() > 0)
	      out.println("First item in my feed: " + myFeed.getData().get(0).getMessage());
	  }

	  void query() {
	    out.println("* FQL Query *");

	    List<FqlUser> users =
	        fbClient.executeQuery("SELECT uid, name FROM user WHERE uid=220439 or uid=7901103", FqlUser.class);

	    out.println("User: " + users);
	  }

	  void multiquery() {
	    out.println("* FQL Multiquery *");

	    Map<String, String> queries = new HashMap<String, String>();
	    queries.put("users", "SELECT uid, name FROM user WHERE uid=220439 OR uid=7901103");
	    queries.put("likers", "SELECT user_id FROM like WHERE object_id=122788341354");

	    MultiqueryResults multiqueryResults = fbClient.executeMultiquery(queries, MultiqueryResults.class);

	    out.println("Users: " + multiqueryResults.users);
	    out.println("People who liked: " + multiqueryResults.likers);
	  }

	  /**
	   * Holds results from an "executeQuery" call.
	   * <p>
	   * Be aware that FQL fields don't always map to Graph API Object fields.
	   */
	  public static class FqlUser {
	    @Facebook
	    String uid;

	    @Facebook
	    String name;

	    @Override
	    public String toString() {
	      return format("%s (%s)", name, uid);
	    }
	  }

	  /**
	   * Holds results from an "executeQuery" call.
	   * <p>
	   * Be aware that FQL fields don't always map to Graph API Object fields.
	   */
	  public static class FqlLiker {
	    @Facebook("user_id")
	    String userId;

	    @Override
	    public String toString() {
	      return userId;
	    }
	  }

	  /**
	   * Holds results from a "multiquery" call.
	   */
	  public static class MultiqueryResults {
	    @Facebook
	    List<FqlUser> users;

	    @Facebook
	    List<FqlLiker> likers;
	  }

	  void search() {
	    out.println("* Searching connections *");

	    Connection<Post> publicSearch =
	        fbClient.fetchConnection("search", Post.class, Parameter.with("q", "watermelon"),
	          Parameter.with("type", "post"));

	    Connection<User> targetedSearch =
	        fbClient.fetchConnection("me/home", User.class, Parameter.with("q", "Mark"),
	          Parameter.with("type", "user"));

	    if (publicSearch.getData().size() > 0)
	      out.println("Public search: " + publicSearch.getData().get(0).getMessage());

	    out.println("Posts on my wall by friends named Mark: " + targetedSearch.getData().size());
	  }

	  void metadata() {
	    out.println("* Metadata *");

	    User userWithMetadata = fbClient.fetchObject("me", User.class, Parameter.with("metadata", 1));

	    out.println("User metadata: has albums? " + userWithMetadata.getMetadata().getConnections().hasAlbums());
	  }

	  void paging() {
	    out.println("* Paging support *");

	    Connection<User> myFriends = fbClient.fetchConnection("me/friends", User.class);
	    Connection<Post> myFeed = fbClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));

	    out.println("Count of my friends: " + myFriends.getData().size());

	    if (myFeed.getData().size() > 0)
	      out.println("First item in my feed: " + myFeed.getData().get(0));

	    for (List<Post> myFeedConnectionPage : myFeed)
	      for (Post post : myFeedConnectionPage)
	        out.println("Post from my feed: " + post);
	  }

	  void selection() {
	    out.println("* Selecting specific fields *");

	    User user = fbClient.fetchObject("me", User.class, Parameter.with("fields", "id,name"));

	    out.println("User name: " + user.getName());
	  }

	  void parameters() {
	    out.println("* Parameter support *");

	    Date oneWeekAgo = new Date(currentTimeMillis() - 1000L * 60L * 60L * 24L * 7L);

	    Connection<Post> filteredFeed =
	        fbClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 3),
	          Parameter.with("until", "yesterday"), Parameter.with("since", oneWeekAgo));

	    out.println("Filtered feed count: " + filteredFeed.getData().size());
	  }

	  void rawJsonResponse() {
	    out.println("* Raw JSON *");
	    out.println("User object JSON: " + fbClient.fetchObject("me", String.class));
	  }
}
