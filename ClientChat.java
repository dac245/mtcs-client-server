import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * ClientChat.java
 * 
 * Copyright 2015 David Camacho
 * 
 * This program makes the client that connects to the server so that it can chat with other clients
 * It uses a GUI to send information to the client and from the client
 * 
 */


public class ClientChat extends JFrame implements ActionListener{
	private JTextArea jtaSendText;
	private JTextArea jtaRecvText;
	private JButton jbSend;
	private JButton jbExit;
	private JButton jbConnect;
	private Socket socket = null;
	private PrintWriter pw = null;
	private BufferedReader br = null;
	ClientThread clientThread = null;
	
	
	public static void main (String args[]) {
		ClientChat cc = new ClientChat();	// starts the ClientChat constructor
	}
	
	/**
	 * ClientChat constructor makes the GUI to interact with 
	 * 
	 */
	
	public ClientChat(){
		super("Chat Client");
		JPanel topP = new JPanel();
		JPanel middleP = new JPanel();
		JPanel bottomP = new JPanel();
		topP.setLayout(new FlowLayout());
		middleP.setLayout(new GridLayout(0,2));
		bottomP.setLayout(new FlowLayout());
		
		JLabel title = new JLabel("Chatroom Client");
		Font f = new Font("Sans", Font.BOLD, 20);
		title.setFont(f);
		topP.add(title);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		jtaRecvText = new JTextArea(30,10);
		jtaSendText = new JTextArea(30,10);
		jtaRecvText.setBorder(border);
		jtaSendText.setBorder(border);
		jtaSendText.setLineWrap(true);
		jtaSendText.setWrapStyleWord(true);
		jtaRecvText.setLineWrap(true);
		jtaRecvText.setWrapStyleWord(true);
		jtaRecvText.setEditable(false);
		middleP.add(jtaRecvText);
		middleP.add(jtaSendText);
		
		jbSend = new JButton("Send");
		jbExit = new JButton("Exit");
		jbConnect = new JButton("Connect");
		
		jbConnect.addActionListener(this);
		jbSend.addActionListener(this);
		jbExit.addActionListener(this);
		
		bottomP.add(jbConnect);
		bottomP.add(jbSend);
		bottomP.add(jbExit);
		
		add(topP, BorderLayout.NORTH);
		add(middleP, BorderLayout.CENTER);
		add(bottomP, BorderLayout.SOUTH);
		
		setSize(550,400);
		setLocation(200,300);
		setResizable(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent ae){
		
			/**
			 *  Once a client clicks the connect button it goes here where we establish the connection
			 *  and then starts the ClientThread class where we use threads to read from the server.
			 * 
			 */
			if(ae.getActionCommand().equals("Connect")){
			try
			{ 
				socket = new Socket("localhost", 16457);
				jtaRecvText.append("Connected: " + socket);
				jtaRecvText.append("\n");
				br   = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				clientThread = new ClientThread();
				clientThread.start();
			}
			catch(UnknownHostException uhe)
			{  
				jtaRecvText.append("Host unknown: " + uhe.getMessage()); 
			}
			catch(IOException ioe)
			{  
				jtaRecvText.append("Unexpected exception: " + ioe.getMessage()); 
			}
		}
		
			/**
			 *  When the user writes something to the JTextArea and then clicks send it 
			 *  sends the string to the server and then resets the text.
			 * 
			 */
		
			if(ae.getActionCommand().equals("Send")){
				try{
						String sendMsg;
						sendMsg = jtaSendText.getText();
						
						pw.println(sendMsg);
						pw.flush();
						
						jtaSendText.setText("");
						
						
				}
				catch(Exception e){
					jtaRecvText.append("Something messed up");
				}
			}
			
			/**
			 *  When the user clicks this button it closes the connection and exits the GUi
			 */
			
			if(ae.getActionCommand().equals("Exit")){
				try{
					socket.close();
					System.exit(0);	
				}
				catch(Exception e){
					jtaRecvText.append("Could not exit");
				}
			}
	}
	
	class ClientThread extends Thread{
		String getMsg;
		
		public ClientThread(){

		}
		/**
		 * Once the thread is started when the user clicks connect it goes here to
		 * loop the readline so we can get the information from the server and put it in the
		 * the JTextArea.
		 * 
		 */
		public void run(){
			
			while(true){
				try{
					getMsg = br.readLine();
					jtaRecvText.append(getMsg + "\n");
				}
				catch(Exception e){
					jtaRecvText.append("Something went wrong.");
				}
			}
		}
		
	}
}
