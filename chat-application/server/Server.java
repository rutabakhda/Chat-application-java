package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import common.TextMessage;
/**
 * server's main class. accepts incoming connections and allows broadcasting
 */
public class Server {

	/**
	 * list of all known connections
	 */
	protected HashSet<Connection> connections = new HashSet<Connection>();
	
	public static void main(String args[]) throws IOException {
		if (args.length != 1)
			throw new RuntimeException("Syntax: ChatServer <port>");
		new Server(Integer.parseInt(args[0]));
	}

	/**
	 * awaits incoming connections and creates Connection objects accordingly.
	 * 
	 * @param port
	 *            port to listen on
	 */
	public Server(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		System.out.println("Initialized Logger");
		Socket client;
		String textColor;
		
		while (true) {
			String password;
			textColor = getColor();
			textColor = rotify(textColor);
			ObjectInputStream is = null;
			ObjectOutputStream os = null;
			System.out.println("Waiting for Connections...");
			client = server.accept();
			Object msg1 = null;
			 is = new ObjectInputStream((client.getInputStream()));
			 os = new ObjectOutputStream(client.getOutputStream());
			 boolean isLogin = false;
			 while(!isLogin)
		     {
		    	 os.writeObject(new TextMessage(rotify("Please enter password")));
		    	 try {
		    	 msg1 = is.readObject();
		    	 } catch(Exception e) {}
				  password = ((TextMessage) msg1).getContent();
				  password = "test123";
		            if (password.equals("test123")) {
		              isLogin = true;
		             } else {
		              os.writeObject(new TextMessage(rotify("you have entered the wrong password")));
		            }
		     }

			System.out.println("Accepted from " + client.getInetAddress());
			Connection c = connectTo(client,is,os,textColor);
			
			c.start();
		}
	}

	public String rotify( String inputString) { //Method to encrypt/decrypt string
		String outputString = ""; //make empty string
		for (int i = 0; i < inputString.length(); i++) {
			char convertedChar = charRot(inputString.charAt(i)); //go through the inputString and then encode each letter
			outputString += convertedChar; //add the character to the above string
		}
		return outputString;
	}

	public char charRot(char charInput) { //Method to encrypt/decrypt a single character
		char[] alphabets = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z' }; //alphabets array to check if the string contains a character
		char convertedChar = charInput;
		boolean isUppercase = !(convertedChar == (Character.toLowerCase(convertedChar))); //check if the current letter is uppercase

		for (int i = 0; i < 26; i++) {
			if (Character.toLowerCase(charInput) == alphabets[i]) { //convert charInput into lower case (to match with the array above)
				int convertedPositionInArray = i + 13; //ROT13 (add 13)

				if (convertedPositionInArray >= 26) { //if its over 'z' in the array, it resets it to 'a'
					convertedPositionInArray -= 26;
				}

				if (isUppercase) { //return character to uppercase
					convertedChar = Character.toUpperCase(alphabets[convertedPositionInArray]); //get character (uppercase)
				} else
					convertedChar = alphabets[convertedPositionInArray]; //get character (lowercase)
			}
		}
		return convertedChar;
	}

	/**
	 * creates a new connection for a socket
	 * 
	 * @param socket
	 *            socket
	 * @return the Connection object that handles all further communication with
	 *         this socket
	 */
	public String getColor() {
		String[] color = { "red", "blue", "green","black","purple","white" };
		int rnd = new Random().nextInt(color.length);
	    return color[rnd];
	}
	public Connection connectTo(Socket socket,ObjectInputStream is,ObjectOutputStream os,String textColor) {
		Connection connection = new Connection(socket,is,os,textColor,this);
		connections.add(connection);
		return connection;
	}

	/**
	 * send a message to all known connections
	 * 
	 * @param text
	 *            content of the message
	 */
	public void broadcast(String text) {
		synchronized (connections) {
			for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext();) {
				Connection connection = (Connection) iterator.next();
				connection.send(text);
			}
		}
	}
	
	/**
	 * remove a connection so that broadcasts are no longer sent there.
	 * 
	 * @param connection
	 *            connection to remove
	 */
	public void removeConnection(Connection connection) {
		connections.remove(connection);
	}

}
