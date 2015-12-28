/*
 Copyright (c) 2015 Azzam Enajjar
 This project is licensed under the terms of the MIT license. 
 Please see LICENSE.md for full license terms.
 */

package CS594.irc;

import CS594.irc.ClientThreads;

public class Rooms {

    private String name;
    private int size;
    public ClientThreads[] clients;

    public Rooms(String name, int size) {
        this.name = name;
        this.size = size;
        clients = new ClientThreads[size]; // An array for holding clients in the specified room

    }

    public ClientThreads[] getAllClients() {
        return clients;
    }

    public String getRoomName() {
        return name;
    }

    public int getRoomSize() {
        return size;
    }

    public void newClient(ClientThreads client) {

        int i;
        for (i = 0; i < size; i++) {
            if (clients[i] == null) {
                clients[i] = client;
                break;
            }
        }
    }

    public int availSizeLeft() {
        int i = 0;
        int roomSize = 0;
        for (i = 0; i < size; i++) {
            if (clients[i] == null) {
                roomSize++;
            }
        }
        return roomSize;
    }
}
