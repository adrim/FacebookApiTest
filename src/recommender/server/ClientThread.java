package recommender.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import recommender.core.FBConcertsRecommender;

public class ClientThread extends Thread {
	private static final String res			= "resources/";
	private Socket 							socket		= null;
	private FBConcertsRecommender recommender = null;
	public ClientThread(Socket socket, FBConcertsRecommender fb) {
		this.socket 	= socket;
		this.recommender = fb;
	}
   
	public void run() {
		try {
        	System.out.println("[Server] New connection accepted: address="	+
        						socket.getInetAddress() + ": "		+
        						socket.getPort());
        	/* Open streams for reading and writing */
		    BufferedReader recv = new BufferedReader(
									new InputStreamReader(
										this.socket.getInputStream()));
		    PrintWriter    send = new PrintWriter(
		    						this.socket.getOutputStream());
		    
		    String sendJson = recommender.recommend(recv.readLine());
		    send.println(sendJson);
		    recommender.saveState();
		    
        	/* Close the connection */
        	recv.close();
        	send.close();
        	socket.close();
		} catch (IOException e){
			//e.printStackTrace();
		}
	}
}

