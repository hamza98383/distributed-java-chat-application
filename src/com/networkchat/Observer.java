package com.networkchat;

/**
 *The Observer design pattern is adhered to by the Observer interface.
 *An entity that watches or listens for updates from a subject—usually
 *the server or another system entity—is defined using this technique

 * To manage incoming messages or alerts, each class that implements
 * this interface needs to include an implementation
 * of the `update()` method.
 */
public interface Observer {
    /**
     * Every time the subject (observable) notifies its observers,
     * this method is called
     *
     * The methods for handling the received message should be specified
     * by the classes that implement this interface.
     *
     * @param message, The observer will receive the update or message.
     */
    void update(String message);
}
