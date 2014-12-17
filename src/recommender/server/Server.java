/**
 * 
 */
package recommender.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import recommender.core.FBConcertsRecommender;

/**
 * @author Adriana
 *
 */
public class Server {
	private ServerSocket serverSock = null;
	private Integer 	 serverPort = 16587;
	private FBConcertsRecommender recommender = new FBConcertsRecommender();;
	
	public void print(String name) {
		System.out.println("Client " + name + " is connected");
	}

	public void run() {
		Socket clientSocket;
		try {
			serverSock = new ServerSocket(serverPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			System.out.println("[Server] Waiting for clients on port " + 
								serverPort);
			try {
				clientSocket = serverSock.accept();
				new ClientThread(clientSocket, recommender).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}