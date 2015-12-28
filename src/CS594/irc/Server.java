/*
 Copyright (c) 2015 Azzam Enajjar
 This project is licensed under the terms of the MIT license. 
 Please see LICENSE.md for full license terms.
 */

package CS594.irc;

import CS594.irc.ClientThreads;
import CS594.irc.Rooms;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
	
	public static BufferedReader inStream = null;
    public static PrintStream outStream = null;
	private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    public static ClientThreads client;

    public static void main(String args[]) throws IOException {

        System.out.println("##################################");
        System.out.println("Internet Relay Chat System");
       System.out.println("##################################");
        
        int i = 0;
        int NumOfRooms = 0;
        Rooms room;
       
        ArrayList<Rooms> rooms = new ArrayList<Rooms>();
        ArrayList<Rooms> cRoom = new ArrayList<Rooms>();

        try {
            serverSocket = new ServerSocket(6789);          
        } catch (IOException e) {
            System.out.println(e);
        }
        
        System.out.println("\nThe server is ready...");
        
        while (true) { // Creating a new socket for each connecting client (thread)
            clientSocket = serverSocket.accept();
            inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outStream = new PrintStream(clientSocket.getOutputStream());
            try {
                outStream.println("##########################################");
                outStream.println("Welcome to the Internet Relay Chat System");
                outStream.println("##########################################");
                outStream.println("Type 1 to create room(s)");
                outStream.println("Type 2 to list all active room(s)");
                outStream.println("Type 3 to join a room");
                outStream.println("##########################################");             
                int chooseFromMenu = Integer.parseInt(inStream.readLine());

                switch (chooseFromMenu) { // Creating a new room
                    case 1: {
                    	outStream.println("Enter the number of rooms you want to create:");
                        int cNoOfRooms = Integer.parseInt(inStream.readLine());
                        String cRoomName = "";
                        int CRoomSize = 0;
                        int j = i;
                        int max = cNoOfRooms + i;
                        while (j < max) {
                        	outStream.println("Enter a room name for the room number: " + (j + 1));
                        	cRoomName = inStream.readLine().trim();
                            outStream.println("Enter the capacity for the room number: " + (j + 1));
                            CRoomSize = Integer.parseInt(inStream.readLine());
                            room = new Rooms(cRoomName, CRoomSize);
                            rooms.add(room);
                            j++;
                        }
                        NumOfRooms = j;
                    }

                   outStream.println("\n##########################################");
                   outStream.println("Type 1 to create room(s)");
                   outStream.println("Type 2 to list all active room(s)");
                   outStream.println("Type 3 to join a room");
                   outStream.println("##########################################");
                   chooseFromMenu = Integer.parseInt(inStream.readLine());

                    case 2: { // Listing all available rooms
                        i = 0;
                        int currentClients = 0;
                        for (i = 0; i < NumOfRooms; i++) {
                        	currentClients = rooms.get(i).getRoomSize() - rooms.get(i).availSizeLeft();
                            outStream.println((i + 1) + " : " + rooms.get(i).getRoomName() + " has " + currentClients + " clients out of " + rooms.get(i).getRoomSize());
                        }

                    }
                    outStream.println("\n##########################################");
                    outStream.println("Type 1 to create room(s)");
                    outStream.println("Type 2 to list all active room(s)");
                    outStream.println("Type 3 to join a room");
                    outStream.println("##########################################");
                    chooseFromMenu = Integer.parseInt(inStream.readLine());
                   
                    case 3: { // Joining room(s)
                        i = 0;
                        String nickName;
                        // listing all available rooms before joining
                        int currentClients = 0;
                        for (i = 0; i < NumOfRooms; i++) {
                            currentClients = rooms.get(i).getRoomSize() - rooms.get(i).availSizeLeft();
                            outStream.println((i + 1) + " : " + rooms.get(i).getRoomName() + " has " + currentClients + " users out of " + rooms.get(i).getRoomSize());
                        }                      
                        outStream.println("Please enter your nickname:\n");
                        nickName = inStream.readLine();
                              outStream.println("Enter the number of rooms you want to join:");
                        int NoOfRooms = Integer.parseInt(inStream.readLine());
                        if (cRoom.isEmpty()) {
                            for (int r = 0; r < NoOfRooms; r++) {
                            	outStream.println("Enter the room number you want to join:");
                                int selectedRoom = Integer.parseInt(inStream.readLine());
                                selectedRoom--;                               
                                for (int c = 0; c < rooms.size(); c++) { // adding a room to the client's rooms list
                                    if (rooms.get(c).equals(rooms.get(selectedRoom))) {
                                        cRoom.add(rooms.get(c));
                                    }
                                }
                            }
                        } else {
                        	cRoom.clear();
                            for (int r = 0; r < NoOfRooms; r++) {
                            	outStream.println("\nEnter the room number you want to join:");
                                int selectedRoom = Integer.parseInt(inStream.readLine());
                                selectedRoom--;
                                for (int c = 0; c < rooms.size(); c++) {
                                    if (rooms.get(c).equals(rooms.get(selectedRoom))) {
                                    	cRoom.add(rooms.get(c));
                                    }
                                }
                            }
                        }

                        // Running a new client thread
                        (client = new ClientThreads(nickName, clientSocket, cRoom, rooms)).start();
                    }

                }

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

}
