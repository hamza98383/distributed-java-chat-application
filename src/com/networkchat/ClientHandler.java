package com.networkchat;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.Socket;

/**
 * Communication between a client and the server is
 * handled by the ClientHandler class.
 * implements Observer to deliver updates to clients and receive notifications
 * implements Runnable to enable concurrent client processing using threads,
 *
 */

public class ClientHandler implements Runnable, Observer {
    // The client receives messages via PrintWriter.
    private PrintWriter out; // Kept private for encapsulation.
    // BufferedReader is used to read client communications.
    private BufferedReader in;
    // A distinct client identification number (established following successful registration)
    protected String clientId;
    // Communication socket with the client
    protected Socket socket;

    /**
     * The client's socket is used by the constructor to initialise the ClientHandler.
     *
     * @param socket The socket connected to the client.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Subclasses (such as TestClientHandler) can set a custom output stream using
     * a protected setter, which is helpful for testing.
     *
     * @param out The PrintWriter to be used for output.
     */
    protected void setOutput(PrintWriter out) {
        this.out = out;
    }

    /**
     * A convenience method that communicates with the customer Direct access
     * to the private 'out' field is avoided.
     *
     * @param message The message to be sent.
     */
    public void println(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /**
     * Verifies whether the socket connection with the client is still operational.
     *
     * @return false otherwise, true if the socket is open.
     */
    public boolean isAlive() {
        return !socket.isClosed();
    }

    /**
     * Client registration, message processing, and disconnection
     * are all handled by the main method that is called when the thread begins.
     */
    @Override
    public void run() {
        boolean registered = false;
        try {
            in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (!registered) {
                out.println("Enter the Client's ID:");
                clientId = in.readLine();
                if (clientId == null) {
                    return;
                }
                if (!Server.getInstance().addClient(clientId, this)) {
                    out.println("The client ID is already in use. Kindly provide a distinct client ID.");
                } else {
                    registered = true; // Registered successfully
                }
            }

            // Inform the other clients and the server of the new connection.
            System.out.println(clientId + " joined the chat");
            // Verify whether the client is the coordinator and let them know.
            if (clientId.equals(Server.getInstance().retrieveCoordinatorId())) {
                out.println("You are assigned as the coordinator.");
                System.out.println(clientId + " is the coordinator.");
            } else {
                // Tell the client who the coordinator is at the moment
                out.println("Present coordinator: " + Server.getInstance().retrieveCoordinatorId());
            }

            // Listen for client instructions at all times
            String command;
            while ((command = in.readLine()) != null) {
                if (command.equalsIgnoreCase("q")) {
                    break; // The connection is terminated with the Quit command
                } else if (command.equalsIgnoreCase("b")) {
                    // Send a broadcast message to every client
                    String messageContent = in.readLine();
                    Server.getInstance().broadcastMessage(clientId, messageContent);
                } else if (command.equalsIgnoreCase("p")) {
                    // Send a specific client a private message
                    String recipient = in.readLine();
                    String messageContent = in.readLine();
                    sendPrivateMessage(clientId, recipient, messageContent);
                } else if (command.equalsIgnoreCase("list")) {
                    // Provide the client making the request with the list of active clients
                    sendClientList();
                }
            }
        } catch (Exception e) {
            // Handle unforeseen failures or disconnections
            System.out.println(clientId + " abruptly disconnected");
        } finally {
            // After disconnecting, remove the client from the server's list of active clients
            if (registered) {
                Server.getInstance().removeClient(clientId);
            }
            try {
                // Try to gracefully terminate the socket connection
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sends a specific client a private message
     *
     * @param sender, sender's ID
     * @param recipient, recipient's ID
     * @param content, private message's contents
     */
    public void sendPrivateMessage(String sender, String recipient, String content) {
        // From the server's client list, retrieve the handler for the recipient
        Message message = new Message(sender, recipient, Message.MessageType.PRIVATE, content);
        // From the server's client list, retrieve the handler for the recipient
        ClientHandler client = Server.getInstance().getClients().get(recipient);
        if (client != null) {
            // If the recipient is located, send them a private message.
            client.println(message.toString());
        } else {
            // If the receiver cannot be located, notify the sender.
            out.println("User " + recipient + " not found.");
        }
    }

    /**
     * provides the requesting client with a list of all active clients
     * along with their connection information.
     */
    public void sendClientList() {
        out.println("Active Members:");
        for (String id : Server.getInstance().getClients().keySet()) {
            ClientHandler handler = Server.getInstance().getClients().get(id);
            String info = handler.getClientInfo();
            if (id.equals(Server.getInstance().retrieveCoordinatorId())) {
                out.println(id + " (" + info + ") [Coordinator]");
            } else {
                out.println(id + " (" + info + ")");
            }
        }
    }

    /**
     * gives back the client's connection information (IP address and port)
     *
     * @return a string containing the connection information for the client.
     */
    public String getClientInfo() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    /**
     * Implements the Observer update method.
     * provides the client with a message update.
     *
     * @param message The message to be sent to the client.
     */
    @Override
    public void update(String message) {
        out.println(message);
    }
}
