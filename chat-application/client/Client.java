package client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import common.TextMessage;

/**
 * simple chat client
 */
public class Client implements Runnable {
	
	protected ObjectInputStream inputStream;
	protected ObjectOutputStream outputStream;
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	
	protected Thread thread;
	
	public static void main(String args[]) throws IOException {
		if (args.length != 2)
			throw new RuntimeException("Syntax: ChatClient <host> <port>");

		Client client = new Client(args[0], Integer.parseInt(args[1]));
		
		// call user interface here
		new Console(client);
	}

	
	public Client(String host, int port) {
		try {
			System.out.println("Connecting to " + host + " (port " + port + ")...");
			
			Socket s = new Socket(host, port);
			
			this.outputStream = new ObjectOutputStream((s.getOutputStream()));
			this.inputStream = new ObjectInputStream((s.getInputStream()));
			
			thread = new Thread(this);
			thread.start();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * main method. waits for incoming messages.
	 */
	public void run() {
		try {
			Thread thisthread = Thread.currentThread();
			while (thread == thisthread) {
				try {
					Object msg = inputStream.readObject();
					handleIncomingMessage(msg);
				} catch (EOFException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			thread = null;
			try {
				outputStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * decides what to do with incoming messages
	 * 
	 * @param msg
	 *            the message (Object) received from the sockets
	 */
	private String derotify( String inputString) { //Method to encrypt/decrypt string
		String outputString = ""; //make empty string
		for (int i = 0; i < inputString.length(); i++) {
			char convertedChar = charDeRot(inputString.charAt(i)); //go through the inputString and then encode each letter
			outputString += convertedChar; //add the character to the above string
		}
		return outputString;
	}

	public char charDeRot(char charInput) { //Method to encrypt/decrypt a single character
		char[] alphabets = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z' }; //alphabets array to check if the string contains a character
		char convertedChar = charInput;
		boolean isUppercase = !(convertedChar == (Character.toLowerCase(convertedChar))); //check if the current letter is uppercase

		for (int i = 0; i < 26; i++) {
			if (Character.toLowerCase(charInput) == alphabets[i]) { //convert charInput into lower case (to match with the array above)
				int convertedPositionInArray = i - 13; //ROT13 (add 13)
				String s = Integer.toBinaryString(convertedPositionInArray);
				
				if (s.length()==32 & s.startsWith("1")) { //if its over 'z' in the array, it resets it to 'a'
					convertedPositionInArray += 26;
				}

				if (isUppercase) { //return character to uppercase
					convertedChar = Character.toUpperCase(alphabets[convertedPositionInArray]); //get character (uppercase)
				} else
					convertedChar = alphabets[convertedPositionInArray]; //get character (lowercase)
			}
		}
		return convertedChar;
	}

	protected void handleIncomingMessage(Object msg) {
		if (msg instanceof TextMessage) {
			String decryptedText = derotify(((TextMessage) msg).getContent());
			fireAddLine(decryptedText + "\n");
		}
	}

	public void send(String line) {
		String encryptedText = rotify(line);
		send(new TextMessage(encryptedText));
	}
	
	private String rotify( String inputString) { //Method to encrypt/decrypt string
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


	public void send(TextMessage msg) {
		try {
			outputStream.writeObject(msg);
			outputStream.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
			this.stop();
		}
	}
	
	/**
	 * listener-list for the observer pattern
	 */
	private ArrayList<ChatLineListener> listeners = new ArrayList<ChatLineListener>();

	/**
	 * addListner method for the observer pattern
	 */
	public void addLineListener(ChatLineListener listener) {
		listeners.add(listener);
	}

	/**
	 * remove Listner method for the observer pattern
	 */
	public void removeLineListener(ChatLineListener listner) {
		listeners.remove(listner);
	}

	/**
	 * fire Listner method for the observer pattern
	 */
	public void fireAddLine(String line) {
		for (Iterator<ChatLineListener> iterator = listeners.iterator(); iterator.hasNext();) {
			ChatLineListener listener = (ChatLineListener) iterator.next();
			listener.newChatLine(line);
		}
	}

	public void stop() {
		thread = null;
	}
}
