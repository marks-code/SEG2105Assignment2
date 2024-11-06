// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param loginID The string that denominates the login id
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginID) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.startsWith("#")) {
    		handleCommand(message);
    	}
    	else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String command) {
	  if (command.equals("#quit")) {
		  quit();
	  }
	  if (command.equals("#logoff")) {
		  if (isConnected()) {
			  try {
				  closeConnection();
				  System.out.println("Disconnected from server");
			  } catch (IOException e) {
				  System.out.println("Error logging off: " + e.getMessage());
			  }
		  }
		  else {
			  System.out.println("Error: client is not connected");
		  }
	  }
	  if (command.equals("#sethost")) {
		  String[] commandFormatted = command.trim().split(" ");
          if (commandFormatted.length > 1) {
              if (!isConnected()) {
                  setHost(commandFormatted[1]);
                  System.out.println("Host set to: " + commandFormatted[1]);
              }
              else {
                  System.out.println("Error: Must log off before setting host.");
              }
          } else {
              System.out.println("Error: No host specified.");
          }
	  }
	  if (command.equals("#setport")) {
		  String[] commandFormatted = command.trim().split(" ");
          if (commandFormatted.length > 1) {
              try {
                  if (!isConnected()) {
                      setPort(Integer.parseInt(commandFormatted[1]));
                      System.out.println("Port set to: " + Integer.parseInt(commandFormatted[1]));
                  } else {
                      System.out.println("Error: Must log off before setting port.");
                  }
              } catch (NumberFormatException e) {
                  System.out.println("Error: Invalid port number.");
              }
          } else {
              System.out.println("Error: No port specified.");
          }
		  
	  }
	  if (command.equals("#login")) {
		  if (isConnected()) {
			  System.out.println("Error: client already connected");
		  }
		  else {
			  try {
				  openConnection();
				  System.out.println("Connected to server at " + getHost() + " with port " + getPort());
			  } catch (IOException e) {
				  System.out.println("Error connecting to server: " + e.getMessage());
			  }
		  }
	  }
	  if (command.equals("#gethost")) {
		  System.out.println("Current host: " + getHost());
	  }
	  if (command.equals("#getport")) {
		  System.out.println("Current port: " + getPort());
	  }
  }
  
  /**
   * This method is called after a successful connection to the server.
   * It automatically sends the #login <loginId> message to the server.
   */
  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginID);
    } catch (IOException e) {
      clientUI.display("Error: Unable to send login message to server.");
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
   * Implements the hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   * 
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down");
	  quit();
  }
  
  /**
   * Implements the hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection closed");
  }
  
}
//End of ChatClient class
