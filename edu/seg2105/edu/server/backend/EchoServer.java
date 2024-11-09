package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

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
    serverStarted();
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
 * @throws IOException 
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    String message = msg.toString();
    
    if (client.getInfo("loginID") == null) {
      
      if (message.startsWith("#login ")) {
        String loginID = message.substring(7).trim(); 
        
        if (!loginID.isEmpty()) {
          client.setInfo("loginID", loginID); 
          System.out.println("Client logged in with ID: " + loginID);
          try {
			client.sendToClient("Login successful. Welcome " + loginID + "!");
		} catch (IOException e) {
		}
        } else {
          try {
			client.sendToClient("ERROR: Login ID cannot be empty.");
		} catch (IOException e) {
	
		}
          try {
            client.close();
          } catch (IOException e) {
            System.out.println("Error closing client connection: " + e.getMessage());
          }
        }
      } else {
   
        try {
			client.sendToClient("ERROR: You must log in first with #login <loginID>");
		} catch (IOException e) {

		}
        try {
          client.close();
        } catch (IOException e) {
          System.out.println("Error closing client connection: " + e.getMessage());
        }
      }
    } else {
      // Prefix messages from logged-in clients with their login ID
      String loginID = (String) client.getInfo("loginID");
      System.out.println("Message received from " + loginID + ": " + message);
      this.sendToAllClients(loginID + ": " + message);
    }
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
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class