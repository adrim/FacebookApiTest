package fb;

import java.io.PrintStream;




import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Page;
import com.restfb.types.User;

public class FacebookConnect {
	private FacebookClient fbClient = null;
	private FacebookClient publicFbClient = null;
	private final String MY_APP_SECRET = "dd987ca8593b735729a0e0b6093ed52a";
	private final String MY_APP_ID = "769034396515940";
	PrintStream out = System.out;
	private static final long serialVersionUID = -8873437295913761132L;
	private final String myaccesstoken = "CAACEdEose0cBAExbS2FXGjWQ1b1ArXaZAfZB2xS64Tl0jLpA7ULBAZBHkeAgKx0Sts5wsTLWZCiFu0FjBwIwSse90EpqdD2SF14BULqS9qq3yncjx14dZCMSPiDEm7ZBsCZByW7J6AvoYckbHxUroootjiWZAwHAwz4rSX71vscDt16L5XYvxNE5QS4YRBlOn9zbLm24wyHu5fqW9eNsuOZBhNQY5eT6f2gcZD";

	FacebookConnect() {
		fbClient = new LoggedInFacebookClient(MY_APP_ID, MY_APP_SECRET);
//		fbClient = new DefaultFacebookClient();
	}
	
	public void runEverything() {
		fetchObject();
	}
	
	public static void main(String[] args) {
		FacebookConnect m = new FacebookConnect();
		m.runEverything();
	}
	void fetchObject() {
	    out.println("* Fetching single objects *");

	    Page page = fbClient.fetchObject("77696833002/events", Page.class);
	  }
}
