package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
	  
	  
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
	/**
	 * Hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
  	@Override
	protected void clientConnected(ConnectionToClient client) {
		System.out.println(String.format("Client has connected with info: %s", client.toString()));
	}
 
	/**
	 * Hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
  	@Override
	synchronized protected void clientDisconnected(
		ConnectionToClient client) {
  		System.out.println(String.format("Client has disconnected with info: %s", client.toString()));
	}

  	public void handleMessageFromServerUI(String message) {
  	    if (message.startsWith("#")) {
			handleCommand(message);
		}
		else {
			sendToAllClients(message);
		}
  	}

  	private void handleCommand(String command) {
  	    if (command.equals("#quit")) {
  	    	serverUI.display("Server quit gracefully");
  	    	System.exit(0);
  	    } else if (command.equals("#stop")) {
  	        stopListening();
  	        serverUI.display("Server stopped listening for new clients.");
  	    } else if (command.equals("#close")) {
  	        try {
  	            close();
  	            serverUI.display("Server closed and all clients disconnected.");
  	        } catch (IOException e) {
  	        	serverUI.display("Error closing server: " + e.getMessage());
  	        }
  	    } else if (command.startsWith("#setport")) {
  	        if (!isListening()) {
  	            try {
  	                int port = Integer.parseInt(command.split(" ")[1]);
  	                setPort(port);
  	                serverUI.display("Port is now: " + port);
  	            } catch (NumberFormatException e) {
  	            	serverUI.display("Error: Invalid port number");
  	            } catch (ArrayIndexOutOfBoundsException e) {
  	            	serverUI.display("Error: No port specified");
  	            }
  	        } else {
  	            System.out.println("Error: cannot set a new port when server is open");
  	        }
  	    } else if (command.equals("#start")) {
  	        if (!isListening()) {
  	            try {
  	                listen();
  	                serverUI.display("Server started listening for new clients");
  	            } catch (IOException e) {
  	            	serverUI.display("Error (Server cannot start): " + e.getMessage());
  	            }
  	        } else {
  	            System.out.println("Error: Server is already listening");
  	        }
  	    } else if (command.equals("#getport")) {
  	    	serverUI.display("Current port: " + String.valueOf(getPort()));
  	    } else {
  	        serverUI.display("Error: command not in system");
  	    }
  	}

}
//End of EchoServer class