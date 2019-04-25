package client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console implements ChatLineListener,Runnable {
	
	protected Thread thread;
	private Client chatClient;
	
	private BufferedReader terminal;

	public Console(Client chatClient) {
		
		// register listener so that we are informed whenever a new chat message
		// is received (observer pattern)
		chatClient.addLineListener(this);
		this.chatClient = chatClient;
		
		// I/O from console
		this.terminal = new BufferedReader(new InputStreamReader(System.in));
		
		thread = new Thread(this);
		thread.start();		
	}

	@Override
	public void run() {
		try {
			Thread thisthread = Thread.currentThread();
			while (thread == thisthread) {
				try {
					String msg = terminal.readLine();
					if (msg != null || msg != "")
						chatClient.send(msg);
				} catch (EOFException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			thread = null;
		}	
	}

	@Override
	public void newChatLine(String line) {
		System.out.print(line);
		
	}
}