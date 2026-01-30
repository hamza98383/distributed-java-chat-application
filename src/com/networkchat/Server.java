package com.networkchat;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // The server will listen on this configured port.
    public static int CONFIGURED_PORT = 1108;

    // Singleton instance of the Server.
    private static Server instance;

    // Shared state: message history, coordinator, and active clients.
    // Using a synchronized LinkedHashMap to preserve insertion order.
    private final Map<String, ClientHandler> active_clients = Collections.synchronizedMap(new LinkedHashMap<>());
    private volatile String coordinatorId = null;
    private final List<String> message_history = new ArrayList<>();

    // Private constructor for Singleton.
    private Server() { }

    /**
     * Returns the single instance of Server.
     * @return the singleton instance.
     */
    public static synchronized Server getInstance() {
        if (instance == null) {
            // Make a new instance if one hasn't already been formed
            instance = new Server();
        }
        return instance;
    }

    /**
     * Starts the server on the given port.
     * @param port The port number to listen on.
     * @pre port > 1024
     */
    public void initialiseServer(int port) {
        System.out.println("Initialised Server\nPort: " + port);
        // Every 20 seconds, set up an automated task to check for inactive clients
        ExecutorService pool = Executors.newCachedThreadPool();

        // Schedule a Timer task to inspect active clients every 20 seconds (first execution after 20 sec).
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Call the method to find and eliminate inactive clients
                inspectActiveClients();
            }
        }, 20000, 20000); // initial delay of 20 sec, then every 20 sec

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                // For the new client, create a new ClientHandler (using a factory)
                ClientHandler clientHandler = ClientHandlerFactory.createClientHandler(socket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace(); // During socket communication, handle any I/O exceptions
        } finally {
            pool.shutdown();
        }
    }

    /**
     * finds and eliminates clients that are not active from the server
     */
    private synchronized void inspectActiveClients() {
        List<String> inactiveClients = new ArrayList<>();
        // Check if any of the current clients are inactive (that is, not alive) by iterating through them.
        synchronized (active_clients) {
            for (Map.Entry<String, ClientHandler> entry : active_clients.entrySet()) {
                if (!entry.getValue().isAlive()) {
                    //Add inactive clients to the list to be removed.
                    inactiveClients.add(entry.getKey());
                }
            }
        }
        // Get rid of every client that isn't active.
        for (String clientId : inactiveClients) {
            removeClient(clientId);
        }
        printActiveClients();
    }

    /**
     * Prints the list of active clients only if there are any.
     */
    private synchronized void printActiveClients() {
        if (active_clients.isEmpty()) {
            return;
        }
        System.out.println("Active Members: ");
        synchronized (active_clients) {
            for (String clientId : active_clients.keySet()) {
                System.out.println(clientId);
            }
        }
    }

    /**
     * Logs a message with a time-stamp and stores it in the message history.
     * @param message The message to log.
     * @pre message != null
     */
    public synchronized void logMessage(String message) {
        // Retrieve the time-stamp in the format "yyyy-MM-dd HH:mm:ss"
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // Add the timestamp-containing message to the message log
        message_history.add("[" + timestamp + "] " + message);
    }

    /**
     * Adds a new client if the clientId is unique.
     * @param clientId The client's unique identifier.
     * @param handler The client's handler object.
     * @return true if added, false if the clientId is already in use.
     * @pre clientId != null && handler != null
     */
    public synchronized boolean addClient(String clientId, ClientHandler handler) {
        if (active_clients.containsKey(clientId)) {
            return false;
        }
        active_clients.put(clientId, handler);
        if (coordinatorId == null) {
            coordinatorId = clientId;
        }
        // Do not print active clients immediately here.
        return true;
    }

    /**
     * Removes a client from the active clients.
     * @param clientId The ID of the client to remove.
     * @pre clientId != null
     */
    public synchronized void removeClient(String clientId) {
        if (active_clients.containsKey(clientId)) {
            active_clients.remove(clientId);
            System.out.println(clientId + " left the chat");
            // Broadcast to remaining clients.
            notifyClients(clientId + " has left the chat");
            if (clientId.equals(coordinatorId)) {
                setNewCoordinator();
            }
            // Do not print active clients immediately here.
        }
    }

    /**
     * Reassigns the coordinator based on the join order and notifies clients.
     */
    private synchronized void setNewCoordinator() {
        if (!active_clients.isEmpty()) {
            // Because we use a LinkedHashMap, the first inserted client is chosen.
            Map.Entry<String, ClientHandler> newCoordinator = active_clients.entrySet().iterator().next();
            coordinatorId = newCoordinator.getKey();
            System.out.println("New coordinator assigned: " + coordinatorId);
            notifyClients("New coordinator assigned: " + coordinatorId);
            newCoordinator.getValue().println("You are now the new coordinator.");
        } else {
            coordinatorId = null; // Set coordinatorId to null as there are no more clients
        }
    }

    /**
     * Notifies all connected clients (observers) with a message.
     * @param message, The message to broadcast to all clients
     */
    public void notifyClients(String message) {
        synchronized (active_clients) {
            // Iterate through all active clients and send them the message
            for (ClientHandler client : active_clients.values()) {
                client.update(message);
            }
        }
    }

    /**
     * Broadcasts a message to all clients.
     * @param sender The sender's ID.
     * @param content The message content.
     * @pre content != null
     */
    public synchronized void broadcastMessage(String sender, String content) {
        // Create a new Message object with the sender's ID and the content
        Message message = new Message(sender, content);
        // Log the message with a time-stamp
        logMessage(message.toString());
        // Notify all clients with the new message
        notifyClients(message.toString());
    }

    /**
     * Returns a copy of the active clients map.
     * @return a new LinkedHashMap containing all active clients.
     */
    public synchronized Map<String, ClientHandler> getClients() {
        return new LinkedHashMap<>(active_clients);
    }

    /**
     * Returns the current coordinator's ID.
     * @return the coordinatorId.
     */
    public synchronized String retrieveCoordinatorId() {
        return coordinatorId;
    }

    // Main method: entry point of the program.
    //It starts the server on the configured port.
    public static void main(String[] args) {
        int port = CONFIGURED_PORT; // Use the CONFIGURED_PORT value defined at the top of the class.
        Server server = Server.getInstance();
        // Start the server with the chosen port.
        server.initialiseServer(port);
    }
}

