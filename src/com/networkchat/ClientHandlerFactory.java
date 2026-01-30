package com.networkchat;

import java.net.Socket;

/**
 * A factory class for generating ClientHandler objects.
 * The functionality for creating and returning objects is
 * encapsulated in this class using the Factory Design Pattern.
 */

public class ClientHandlerFactory {
    /**
     * For the specified socket, a new ClientHandler is created and returned.
     * This method handles the creation of the ClientHandler instance and
     * verifies that the supplied socket is valid (non-null).
     *
     * @param socket The client-connected socket cannot be null.
     * A connection is made between a certain client
     * and the server using this socket.
     *
     * @return Communication with the connected client is handled by a
     * fresh instance of ClientHandler.
     *
     * @pre socket!= null // Precondition: There must be no null in the socket.
     */
    public static ClientHandler createClientHandler(Socket socket) {
        // Return the newly created ClientHandler object.
        // Managing communication between the server and the connected
        // client will fall under the purview of the ClientHandler.
        return new ClientHandler(socket);
    }
}
