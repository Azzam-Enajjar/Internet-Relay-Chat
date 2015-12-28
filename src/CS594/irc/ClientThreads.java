/*
 Copyright (c) 2015 Azzam Enajjar
 This project is licensed under the terms of the MIT license. 
 Please see LICENSE.md for full license terms.
 */

package CS594.irc;

import CS594.irc.Rooms;

import java.net.Socket;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;

public class ClientThreads extends Thread {
	
    private DataInputStream in = null;
    private PrintStream out = null;
    private Socket clientSocket = null;
    private ClientThreads[] clients;
    public String name;
    ArrayList<Rooms> rooms;
    ArrayList<Rooms> rooms1;
    ArrayList<Rooms> rooms2;
    Rooms room;
    ArrayList<Rooms> cRoom = new ArrayList<Rooms>();

    public ClientThreads(String name, Socket clientSocket, ArrayList<Rooms> rooms, ArrayList<Rooms> rooms2) {
        this.name = name;
        this.clientSocket = clientSocket;
        this.rooms = rooms;
        this.rooms2 = rooms2;
    }

    public void run() {
        int numOfClients;
        boolean endFlag; 
        endFlag = true;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new PrintStream(clientSocket.getOutputStream());
            out.println("#####################################################################");            
            out.println("Client [" + name + "]");            
            out.println("#####################################################################");            
            out.println("Type @Users to list all the connected clients in the room");
            out.println("Type @Private to initiate a private chat session with another client");
            out.println("Type @Leave to leave the room");
            out.println("Type @Exit to disconnect from the server");
            out.println("#####################################################################");            
            if (rooms1 == null) {
                rooms1 = (ArrayList<Rooms>) rooms.clone();
                for (int c = 0; c < rooms1.size(); c++) {
                    room = rooms1.get(c);
                    this.room.newClient(this);
                    clients = this.room.getAllClients();
                    numOfClients = room.getRoomSize();       
                    out.println("You have joined the room: " + room.getRoomName());
                    // Informing all clients in the room that this client has entered the room
                    for (int i = 0; i < numOfClients; i++) {
                        if (clients[i] != null && clients[i] != this) {
                            clients[i].out.println("Client " + name + " has entered the room");
                        }
                    }
                }
                while (endFlag) {
                    String line = in.readLine();     
                    if (line.startsWith("@Leave")) { // Disconnecting from a room when typing @Leave
                        out.println("[" + name + "] You have left the room");
                        for (int c = 0; c < rooms1.size(); c++) {
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (clients[i] == this) {
                                    clients[i] = null;

                                }
                            }
                        }
                       
                        for (int c = 0; c < rooms1.size(); c++) { // Informing all clients in the room that this client has left the room
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (clients[i] != null && clients[i] != this) {
                                    clients[i].out.println("[" + name + "] has left the room");
                                }
                            }
                        }

                        this.rooms1 = null;
                        while (!line.startsWith("@Exit")) {
                            line = in.readLine();
                            if (line.startsWith("@Join")) {
                                int i = 0;
                                int currentClients = 0;
                                // listing rooms
                                for (i = 0; i < rooms2.size(); i++) {
                                    currentClients = rooms2.get(i).getRoomSize() - rooms2.get(i).availSizeLeft();
                                    out.println((i + 1) + " : " + rooms2.get(i).getRoomName() + " has " + currentClients + " clients out of " + rooms2.get(i).getRoomSize());
                                }                        
                                out.println("");
                                out.println("Enter the number of rooms you want to join:");
                                int NoOfRooms = Integer.parseInt(in.readLine());
                                if (cRoom.isEmpty()) {
                                    for (int r = 0; r < NoOfRooms; r++) {
                                        out.println("Enter the room number you want to join:");
                                        int chosen_room = Integer.parseInt(in.readLine());
                                        chosen_room--;
                                        // Adding a room to the client's rooms list
                                        for (int c = 0; c < rooms2.size(); c++) {
                                            if (rooms2.get(c).equals(rooms2.get(chosen_room))) {
                                            	cRoom.add(rooms2.get(c));
                                            }
                                        }
                                    }
                                } else {
                                	cRoom.clear();
                                    for (int r = 0; r < NoOfRooms; r++) {
                                        out.println("");
                                        out.println("Enter the room number you want to join:");
                                        int chosen_room = Integer.parseInt(in.readLine());
                                        chosen_room--;
                                        for (int c = 0; c < rooms2.size(); c++) {
                                            if (rooms2.get(c).equals(rooms2.get(chosen_room))) {
                                            	cRoom.add(rooms2.get(c));
                                            }
                                        }
                                    }
                                }
                                rooms1 = (ArrayList<Rooms>) cRoom.clone();
                                for (Rooms rooms11 : rooms1) {
                                    room = rooms11;
                                    this.room.newClient(this);
                                    clients = this.room.getAllClients();
                                    this.room.newClient(this);
                                }                              
                            }
                        }

                        out.println("Thanks [" + name + "] for using the IRC network");
                        this.in.close();
                        this.out.close();
                        this.clientSocket.close();
                    }

                    if (line.startsWith("@RoomsList")) { // Listing all available rooms
                        int i = 0;
                        int currentClients = 0;
                        for (i = 0; i < rooms2.size(); i++) {
                            currentClients = rooms2.get(i).getRoomSize() - rooms2.get(i).availSizeLeft();
                            out.println((i + 1) + " : " + rooms2.get(i).getRoomName() + " has " + currentClients + " clients out of " + rooms2.get(i).getRoomSize());
                        }
                    }
                 
                    if (line.startsWith("@Join")) { // Joining room(s)
                        int i = 0;
                        // listing rooms
                        int currentClients = 0;
                        for (i = 0; i < rooms2.size(); i++) {
                            currentClients = rooms2.get(i).getRoomSize() - rooms2.get(i).availSizeLeft();
                            out.println((i + 1) + " : " + rooms2.get(i).getRoomName() + " has " + currentClients + " clients out of " + rooms2.get(i).getRoomSize());
                        }                    
                        out.println("");
                        out.println("Enter the number of rooms you want to join:");
                        int NoOfRooms = Integer.parseInt(in.readLine());
                        for (int r = 0; r < NoOfRooms; r++) {
                            out.println("Enter the room number you want to join:");
                            int chosen_room = Integer.parseInt(in.readLine());
                            chosen_room--;
                            // Adding a room to the client's rooms list
                            for (int c = 0; c < rooms2.size(); c++) {
                                if (rooms2.get(c).equals(rooms2.get(chosen_room))) {
                                    rooms2.get(c).newClient(this);
                                    rooms1.add(rooms2.get(c));
                                }
                            }
                        }
                        for (int c = 0; c < rooms1.size(); c++) {
                            room = rooms1.get(c);
                            clients = this.room.getAllClients();
                            numOfClients = room.getRoomSize();
                            out.println("You are in room  " + room.getRoomName());
                            // Informing all clients in the room that this client has entered the room
                            for (i = 0; i < numOfClients; i++) {
                                if (clients[i] != null && clients[i] != this) {
                                    clients[i].out.println("Client " + name
                                            + " has entered the room");

                                }
                            }
                        }
                    }

                    // Disconnecting from the server when typing @Exit
                    if (line.startsWith("@Exit")) {
                    	out.println("Thanks [" + name + "] for using the IRC network");
                        for (int c = 0; c < rooms1.size(); c++) {
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (clients[i] == this) {
                                    clients[i] = null;

                                }
                            }
                        }                    
                        this.in.close();
                        this.out.close();
                        this.clientSocket.close();

                        // Informing all clients in the room that this client has left
                        for (int c = 0; c < rooms1.size(); c++) {
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (clients[i] != null && clients[i] != this) {
                                    clients[i].out.println("[" + name + "] has left the IRC system ");
                                }
                            }
                        }
                    }

                    // Listing all clients in the room 
                    if (line.startsWith("@Users")) {
                        for (int c = 0; c < rooms1.size(); c++) {
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (clients[i] != null && clients[i] != this) {
                                    out.println(clients[i].name);
                                }
                            }
                        }
                    }

                    // Having a private chat
                    if (line.startsWith("@Private") || line.startsWith("@Accept")) {
                        for (int c = 0; c < rooms1.size(); c++) {
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (clients[i] != null && clients[i] != this) {
                                    out.println(clients[i].name);
                                }
                            }

                        }
                        out.println("Type the name of the client you want to chat with");
                        String privatemessage;
                        privatemessage = in.readLine();
                        for (int c = 0; c < rooms1.size(); c++) {
                            this.clients = this.rooms1.get(c).getAllClients();
                            numOfClients = rooms1.get(c).getRoomSize();
                            numOfClients = rooms1.get(c).getRoomSize();
                            for (int i = 0; i < numOfClients; i++) {
                                if (privatemessage.startsWith(clients[i].name)) {
                                    out.println("A private chat session has been initiated with " + privatemessage + "... Type @End to end the session");
                                    privatemessage = in.readLine();
                                    while (!privatemessage.startsWith("@End")) {
                                        privatemessage = in.readLine();
                                        clients[i].out.println("Private message from " + name + " : " + privatemessage);
                                    }
                                }
                            }
                        }
                    }
                   
                    for (int c = 0; c < rooms1.size(); c++) { // Forwarding the message to the client's room 
                        this.clients = this.rooms1.get(c).getAllClients();
                        numOfClients = rooms1.get(c).getRoomSize();
                        for (int i = 0; i < numOfClients; i++) {
                            if (clients[i] != null && clients[i] != this && !line.startsWith("@Users")
                                    && !line.startsWith("@Leave") && !line.startsWith("@Exit")
                                    && !line.startsWith("@Private") && !line.startsWith("@Accept")
                                    && !line.startsWith("@End") && !line.startsWith("@RoomsList")
                                    && !line.startsWith("@Join")) {
                                clients[i].out.println("From " + name + " : " + line);

                            }
                        }
                    }
                }
            } else {
                rooms1.clear();
                rooms1 = (ArrayList<Rooms>) rooms.clone();
                for (int c = 0; c < rooms1.size(); c++) {
                    room = rooms1.get(c);
                    this.room.newClient(this);
                    clients = this.room.getAllClients();
                    numOfClients = room.getRoomSize();
                    out.println("you are in room  " + room.getRoomName());
                    for (int i = 0; i < numOfClients; i++) {
                        if (clients[i] != null && clients[i] != this) {
                            clients[i].out.println("The user " + name
                                    + " entered this room");

                        }
                    }
                }              
            }
        } catch (IOException e) {
        	System.out.println(e);       	
        }
    }
  
}
