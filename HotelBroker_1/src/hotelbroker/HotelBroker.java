/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbroker;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author kwokszeyan
 */
public class HotelBroker  {
    
    private static ServerSocket serverSocket;
    private static final int PORT = 2172;
    
    public static void main(String[] args) throws IOException {
    
        System.out.println("Opening port...\n");
        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException ioEx)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }
        do {
            
            //Wait for client..
            Socket client = serverSocket.accept();
            
            System.out.println("\nNew client accepted.\n");

            ClientHandlerAccessServer handler = new ClientHandlerAccessServer(client);
            handler.start();
        }while (true);
    }

}
