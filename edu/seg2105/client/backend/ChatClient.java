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
  private static final int DEFAULT_PORT = 0;
//Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  static ChatIF clientUI;

private static String loginID; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }
  public static void main(String[] args) {
      String host = "";
      int port = DEFAULT_PORT; 

      try {
          host = args[0]; 
          if (args.length > 1) {
              port = Integer.parseInt(args[1]); 
          }
      } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Host not provided. Usage: java ChatClient <host> [port]");
          System.exit(1);
      } catch (NumberFormatException e) {
          System.out.println("Invalid port number. Using default port: " + DEFAULT_PORT);
      }
      
      try {
    	  new ChatClient(loginID,host, port, clientUI); 
      } catch (IOException e) {
          System.out.println("Error: Couldn't establish a connection to the server.");
      }
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
	  if (message.startsWith("#")) {
	      handleSpecialCommand(message);
	  } 
	  else {
		  try {
	        sendToServer(message);
	      } catch (IOException e) {
	        clientUI.display("Could not send message to server. Terminating client.");
	        quit();
	      }
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
    System.exit(1);
  }
  
  private void handleSpecialCommand(String command) {
	    String[] tokens = command.split(" ", 2);
	    String cmd = tokens[0];

	    switch (cmd) {
	      case "#quit":
	        quit();
	        break;
	      case "#logoff":
	        try {
	          closeConnection();
	        } catch (IOException e) {
	          clientUI.display("Error while logging off.");
	        }
	        break;
	      case "#sethost":
	        if (isConnected()) {
	          clientUI.display("Cannot change host while connected.");
	        } else if (tokens.length > 1) {
	          setHost(tokens[1]);
	          clientUI.display("Host set to " + tokens[1]);
	        } else {
	          clientUI.display("Usage: #sethost <host>");
	        }
	        break;
	      case "#setport":
	        if (isConnected()) {
	          clientUI.display("Cannot change port while connected.");
	        } else if (tokens.length > 1) {
	          try {
	            setPort(Integer.parseInt(tokens[1]));
	            clientUI.display("Port set to " + tokens[1]);
	          } catch (NumberFormatException e) {
	            clientUI.display("Invalid port number.");
	          }
	        } else {
	          clientUI.display("Usage: #setport <port>");
	        }
	        break;
	      case "#login":
	        if (isConnected()) {
	          clientUI.display("Already connected.");
	        } else {
	          try {
	            openConnection();
	            clientUI.display("Logged in.");
	          } catch (IOException e) {
	            clientUI.display("Error: Could not log in.");
	          }
	        }
	        break;
	      case "#gethost":
	        clientUI.display("Current host: " + getHost());
	        break;
	      case "#getport":
	        clientUI.display("Current port: " + getPort());
	        break;
	      default:
	        clientUI.display("Unknown command: " + command);
	    }
	  }
  
  
  
  
  @Override
  protected void connectionEstablished() {
      try {
          sendToServer("#login " + loginID);
      } catch (IOException e) {
          clientUI.display("Error: Could not send login ID to the server.");
      }
  }
  
  
  
  public String getLoginID() {
      return loginID;
  }
  
  
  
  @Override
  protected void connectionClosed() {
      clientUI.display("Server has shut down.");
      quit();
  }
  @Override
  protected void connectionException(Exception exception) {
      clientUI.display("Server connection error: " + exception.getMessage());
      quit();
  }
}
//End of ChatClient class
