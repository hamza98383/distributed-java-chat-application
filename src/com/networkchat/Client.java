package com.networkchat;
//Import necessary Java libraries.
import java.io.*; // For Input/Output operations (BufferedReader, PrintWriter, etc.)
import java.net.*; // For networking operations (Socket, etc.)

/**
 * The Client class establishes a connection with the server and makes communication easier.
 */
public class Client {
    public static void main(String[] args) {
        // Make that the console input is correctly closed after use by using try-with-resources.
        try (BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Request the IP address of the server from the user.
            System.out.print("Enter Server IP Address: ");
            String serverAddress = consoleInput.readLine(); // Read the server IP from the console.

            // Request that the user input the port number of the server.
            System.out.print("Enter Server Port: ");
            int serverPort = Integer.parseInt(consoleInput.readLine()); // Convert a string input to an integer.

            // Use the designated IP address and port to connect to the server
            try (Socket socket = new Socket(serverAddress, serverPort); // Create a socket to connect to the server.
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream from server.
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { // Output stream to send data to the server.

                // ============================
                // Initial Handshake: Client ID
                // ============================

                // Read and show the server's first message, such as "Enter your ID:"
                String serverMessage = in.readLine();
                System.out.println(serverMessage);

                // The client ID should be sent to the server after being read from the console.
                String clientId = consoleInput.readLine();
                out.println(clientId);

                // =========================
                // Handle Duplicate ID Error
                // =========================

                while (true) {
                    serverMessage = in.readLine(); // Read the server's next message
                    if (serverMessage == null) break; // Terminate if no message is received

                    // Request a new client ID from the user if the one that is currently in use is already being used
                    if (serverMessage.startsWith("The client ID is already in use")) {
                        System.out.println(serverMessage); // Print the error message
                        serverMessage = in.readLine(); // Open the prompt to provide a new ID
                        System.out.println(serverMessage);
                        clientId = consoleInput.readLine(); // Read new client ID
                        out.println(clientId); // Provide the server with the updated ID
                    }

                    // Message for Coordinator Assignment
                    else if (serverMessage.startsWith("You are assigned as the coordinator.") ||
                            serverMessage.startsWith("Present coordinator:")) {
                        System.out.println(serverMessage); // If the client takes over as coordinator, let them know
                        break; // If the client connection is successful, end the loop
                    }

                    // Other messages (welcome message, etc.)
                    else {
                        System.out.println(serverMessage);
                    }
                }

                // ==============================
                // Start Message Listening Thread
                // ==============================

                // To asynchronously listen for messages from the server, start a new thread
                new Thread(() -> {
                    try {
                        String response;
                        // Keep an eye out for any new communications from the server
                        while ((response = in.readLine()) != null) {
                            System.out.println(response); // Print the server's messages
                        }
                    } catch (IOException ignored) {
                        // If the connection is lost, disregard any exceptions
                    }
                }).start(); // Launch the thread


                // ==========================
                // Main Loop to Send Commands
                // ==========================

                String command; // User commands are stored in this variable
                while (true) {
                    // Show the user the commands that are accessible
                    System.out.println("Enter 'b' to broadcast message, 'p' to message privately, 'list' for info of all the active members, 'q' to quit:");
                    command = consoleInput.readLine(); // Read the command from the user
                    out.println(command); // Send the command to the server.

                    // If the user wishes to stop, exit the loop
                    if (command.equalsIgnoreCase("q"))
                        break;

                    // ===========================
                    // Broadcast Message Handling
                    // ===========================

                    if (command.equalsIgnoreCase("b")) {
                        // Request that the user input a message to be broadcast
                        System.out.print("Message: ");
                        out.println(consoleInput.readLine()); // Send the server the broadcast message
                    }

                    // ========================
                    // Private Message Handling
                    // ========================

                    else if (command.equalsIgnoreCase("p")) {
                        // Request the recipient's id
                        System.out.print("Recipient: ");
                        out.println(consoleInput.readLine()); // Send recipient ID.

                        // Request the content of the private communication
                        System.out.print("Message: ");
                        out.println(consoleInput.readLine()); // Send content via private message
                    }
                }
            }
        }
        // Address any potential input/output exceptions.
        catch (IOException e) {
            e.printStackTrace(); // In order to debug, print the error.
        }
    }
}
