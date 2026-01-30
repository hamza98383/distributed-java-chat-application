package com.networkchat;

//To save each message's time stamp, import the Date class.
import java.util.Date;

/**
 * A message represented by the Message class may be a private message
 * between clients or a public broadcast.
 */
public class Message {
    /**
     * Enum to specify the type of message.
     * MessageType can be:
     * - BROADCAST: Sent to all clients.
     * - PRIVATE: Sent to a specific client.
     */
    public enum MessageType {
        BROADCAST, // All connected clients received the message
        PRIVATE // A single receiver receives the message
    }


    // ==================
    // Instance Variables
    // ==================
    private final String sender; // The client's ID that sent the message
    private final String recipient; // Null for broadcast messages.
    private final MessageType type; // The message type (private or broadcast)
    private final String content; // The actual content of the message.
    private final Date timestamp; // The time when the message was created.


    // ============
    // Constructors
    // ============

    /**
     * Constructor for broadcast messages.
     * This is a shorthand for creating a broadcast message.
     *
     * @param sender The sender's ID.
     * @param content The content of the message.
     */
    public Message(String sender, String content) {
        // The main constructor is called with:
        // - Sender's ID
        // - recipient null (since it's a broadcast)
        // - MessageType.BROADCAST to indicate a broadcast
        // - content of the message
        this(sender, null, MessageType.BROADCAST, content);
    }

    /**
     * Constructor to create any kind of message (private or broadcast).
     * This constructor adds a time-stamp and sets all pertinent data.
     *
     * @param sender, sender's ID.
     * @param recipien, ID of the recipient (null if broadcast).
     * @param type, kind The message type (private or broadcast).
     * @param content, The message's real content
     */
    public Message(String sender, String recipient, MessageType type, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
        this.content = content;
        this.timestamp = new Date();
    }

    // ==============================
    // Getters for Message Properties
    // ==============================
    /**
     * returns the ID of the sender
     * @return The client's ID from which the message was sent.
     */
    public String retrieveSender() {
        return sender;
    }

    /**
     * returns the ID of the receiver
     * @return The ID of the recipient, or null in the case of a broadcast
     */
    public String retrieveRecipient() {
        return recipient;
    }

    /**
     * Returns the type of the message.
     * @return The type of message (private or broadcast).
     */
    public MessageType retrieveType() {
        return type;
    }

    /**
     * returns the message's contents
     * @return The actual message content
     */
    public String retrieveContent() {
        return content;
    }

    /**
     * Returns the message's creation time stamp.
     * @return The creation time-stamp of the message is represented by the Date object.
     */
    public Date retrieveTimestamp() {
        return timestamp;
    }


    // =================
    // toString() Method
    // =================

    /**
     * returns the message as a formatted string representation.
     * For logging or showing the message on the console, this is helpful.
     *
     * @return a string providing message details that is readable by humans.
     */
    @Override
    public String toString() {
        // In a comprehensible way, format the time stamp.
        String timeStr = "[" + timestamp + "]";

        // Verify whether the communication is private or not.
        if (type == MessageType.PRIVATE) {

            // Private message format: [time-stamp] Content is private between the sender and the recipient.
            return timeStr + " Private from " + sender + " to " + recipient + ": " + content;
            // Otherwise, it's a BROADCAST.
        } else {
            // Broadcast message format: [time-stamp] Content sender
            return timeStr + " " + sender + ": " + content;
        }
    }
}

