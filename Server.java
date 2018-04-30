package imp;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

public class Server extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	//Types of streams - OutputStream and InputStream.
	private ServerSocket server;
	private Socket connection;
	// A socket is a connection btw machines.
	
	
	
	
	public Server () {
		
		super("The best messager ever! ");
		//This sets the title. (Super - JFrame).
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					
					public void actionPerformed(ActionEvent event) {
						
						sendMessage(event.getActionCommand());
						//This returns whatever event was performed in the text field i.e the text typed in.
						userText.setText("");
											
						
					}
					
				
				}
				
				
				
				);
		
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane (chatWindow));
		setSize(800,600);
		setVisible(true);
		
		
	
	
	}
	      //Set Up and Run the Server
	
		public void startRunning() {
			
			try {
				
				server = new ServerSocket(6789, 100); //6789 is the port number for docking(Where to connect). 100 is backlog no, i.e how many people can wait to access it.
				while (true) {
					//This will run forever.
					
					try{
						
						waitForConnection();
						setupStreams();
						whileChatting();
					}
					
					
					catch(EOFException eofException) {
						
						showMessage("\n The server has ended the connection!");
						
					}
					
					finally {
						
						closeAll();
						//Housekeeping.
					}
					
					
				}
				
			}
			
			catch(IOException ioException ) {
				
				ioException.printStackTrace();
				
			}
			
			
			
		}
	
	public void waitForConnection() throws IOException {
		
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		//Once someone wants to connect, it accepts a connection viz. the server to this socket (connection).
		//Then, this socket is created btw server and the client. (It will only be created when there is a connection present.)
		showMessage("Now connected to: " + connection.getInetAddress().getHostName());
		//Get host name converts it to a string.
		
				
	}
		
	public void setupStreams() throws IOException{
		//Get stream to send a receive data
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("All streams are set up and ready to go!");
		
				
	}
	
	public void whileChatting() throws IOException{
		//Code that will be running during the conversation.
		
		String message = "You are now connected. Gear up to chat like never before...";
		sendMessage(message);
		ableToType(true);
		
		do {
			
			try {
				
				message = (String) input.readObject();
				//Anything coming in the input stream is treated as an object and then will be stored in the variable message as a string.
				showMessage("\n" + message);
			
			}
			
			catch(ClassNotFoundException cnfException) {
				
				showMessage ("\n An error occured with the message sent.");
				
			}
			
		}
		
		while (!message.equalsIgnoreCase("CLIENT - END"));
		//While the client has not sent a message saying "END".
		
	}
	
	
	
	
	
	public void closeAll(){
		
		//Close streams and sockets after you are done.
		
		showMessage("\n Closing all connections...");
		ableToType(false);
		try {
			
			input.close();
			output.close();
			connection.close();
			
		}
		catch(IOException e) {
			
			e.printStackTrace();
			
		}
		
		
	}
	
	
	private void showMessage(final String text){
	
		//Updates the chat window.	
		
		SwingUtilities.invokeLater(

				new Runnable () {
					//This is a thread to update the GUI.
					
			public void run () {
				//This is the method that gets called everytime the GUI needs to be updated.
				
				chatWindow.append(text);
				
				
			} 		
										
				}
				
				);
		
		
	}
	private void sendMessage(String message){
		
		try {
			
			output.writeObject("SERVER - " + message);
			output.flush();
			//This clears all the extra bytes and pushes towards the user. 
			showMessage("\nSERVER - " + message);
			
			
		}
		catch(IOException ioe) {
			
			chatWindow.append("An error has occured. Message could not be sent.");
			
		}
		
	}
	
	
public void ableToType(final Boolean tof) {
		
		//This is used to update only parts of the GUI instead of doing the whole process again.
		
		SwingUtilities.invokeLater(

				new Runnable () {
					//This is a thread to update the GUI.
					
			public void run () {
				//This is the method that gets called everytime the GUI needs to be updated.
				
		userText.setEditable(tof);
			}
	
				}
);
		
}
	
		
}
