package edu.seg2105.edu.server.backend;

import edu.seg2105.client.common.ChatIF;
import java.io.*;
import java.util.Scanner;

public class ServerConsole implements ChatIF {
	
	
    private EchoServer server;   
    private Scanner fromConsole; 

 
    public ServerConsole(int port) {
        server = new EchoServer(port);
     

        fromConsole = new Scanner(System.in);
    }

  
    public void accept() {
        try {
            String message;
            while (true) {
                message = fromConsole.nextLine();
                handleServerInput(message);
            }
        } catch (Exception e) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    
    private void handleServerInput(String message) {
    	if(message.startsWith("#")) {
    		handleSpecialCommand(message);
    	}
    	else
    	{
	        display("SERVER MSG> " + message);  
	        server.sendToAllClients("SERVER MSG> " + message);
    	}
    }
    
    @Override
    public void display(String message) {
        if(message.startsWith("#")) {
    		handleSpecialCommand(message);
    	}
    	else
    	{
    		System.out.println(message);
    	}
    }

    private void handleSpecialCommand(String command) {
        if (command.startsWith("#")) {
            String[] tokens = command.split(" ");
            switch (tokens[0]) {
                case "#quit":
                    display("Shutting down the server...");
                    System.exit(0);
                    break;

                case "#stop":
                    server.stopListening();
                    display("Server stopped listening for new clients.");
                    break;

                case "#close":
                    try {
                        server.close();
                        display("Server closed. All clients disconnected.");
                    } catch (IOException e) {
                        display("Error: Could not close server.");
                    }
                    break;

                case "#setport":
                    if (tokens.length > 1) {
                        try {
                            int port = Integer.parseInt(tokens[1]);
                            if (!server.isListening()) {
                                server.setPort(port);
                                display("Port set to " + port);
                            } else {
                                display("Error: Stop the server before changing the port.");
                            }
                        } catch (NumberFormatException e) {
                            display("Invalid port number.");
                        }
                    } else {
                        display("Usage: #setport <port>");
                    }
                    break;

                case "#start":
                    if (!server.isListening()) {
                        try {
                            server.listen();
                            display("Server started listening for new clients.");
                        } catch (IOException e) {
                            display("Error: Could not start listening.");
                        }
                    } else {
                        display("Server is already listening.");
                    }
                    break;

                case "#getport":
                    display("Current port: " + server.getPort());
                    break;

                default:
                    display("Unknown command.");
                    break;
            }
        } else {
            display("SERVER MSG> " + command);
            server.sendToAllClients("SERVER MSG> " + command);
        }
    }

    
    public static void main(String[] args) {
        int port = EchoServer.DEFAULT_PORT; 

        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("No port specified. Using default port: " + port);
        }

        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();
    }
}
//end of ServerConsole