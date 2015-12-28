/*
 Copyright (c) 2015 Azzam Enajjar
 This project is licensed under the terms of the MIT license. 
 Please see LICENSE.md for full license terms.
 */
package CS594.irc;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.io.IOException;

public class Client implements Runnable{
	
    private static Socket clientSocket = null;
    private static PrintStream outStream = null;
    private static DataInputStream inStream = null;
    private static BufferedReader inFromUser = null;
    private static boolean endFlag = true;

    public static void main(String[] args) {

        try {
            clientSocket = new Socket("localhost", 6789);
            inFromUser = new BufferedReader(new InputStreamReader(System.in));
            outStream = new PrintStream(clientSocket.getOutputStream());
            inStream = new DataInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("The host is unknown");
        } catch (IOException e) {
            System.err.println("Connection cannot be established");
        }

        if (clientSocket != null && outStream != null && inStream != null) {
            try {
                new Thread(new Client()).start();
                while (endFlag) {
                    outStream.println(inFromUser.readLine());
                }                         
                outStream.close();
                inStream.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
                
            }

        }
    }

    public void run() { // Starting a thread to get input from the socket     
        String message;
        try {
            while ((message = inStream.readLine()) != null) {
                System.out.println("-> " + message);
                if (message.startsWith("Exit")) { // Stop reading from the socket when "Exit" is received
                    break;
                }
            }
            endFlag = false;                    
        } catch (IOException e) {
            System.err.println("\nThe Server has crashed!");
            System.err.println("You have been disconnected..");           
        }
    }

}
