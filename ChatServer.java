import java.io.*;
import java.util.*;
import java.net.*;

/**
 * ChatServer.java
 * 
 * Copyright 2015 David Camacho
 * 
 * This program creates the server so the users can connect the server and send
 * strings to everyone connected to the server.
 * 
 */ 

public class ChatServer{
    // gets the clients so that we can send the string to each one
    public ArrayList<ServerThread> st = new ArrayList<ServerThread>();
		
    public static void main(String[] args){
        new ChatServer();
    }
		
   /**
     * The ChatServer constructor gets who is connected and then adds them to the arraylist and then
     * we start the thread.
     */
    public ChatServer(){
        ServerSocket ss = null;

        try{
            ss = new ServerSocket(16457);
            Socket socket = null;

            while(true){
                socket = ss.accept();
                ServerThread threaded = new ServerThread(socket);
                st.add(threaded);
                threaded.start();
            }
        }
        catch(Exception e){
            System.out.println("An Error occurred");
        }
    }
		
		
  /**
    * ServerThread has the run method that starts the thread which gets the 
    * strings from the clients and send them to everyone connected to the server.
    */
    class ServerThread extends Thread{
        Socket s = null;
        String clientMsg;
        PrintWriter pw;
        BufferedReader br;

        public ServerThread(Socket _socket){
            s = _socket;    // gets the socket from the other class
        }

        public void run(){
            try{
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

                while((clientMsg = br.readLine()) != null){    // loops while it is reading the message

                    System.out.println("Server read: "+ clientMsg);

                    for(ServerThread get : st){    // for loop to iterate through each client and send them the string
                        get.pw.println(clientMsg);	//to clients
                        get.pw.flush();
                    }
                }
            }
            catch(Exception e){
                System.out.println("Something went wrong");
            }
        }
    }
}
