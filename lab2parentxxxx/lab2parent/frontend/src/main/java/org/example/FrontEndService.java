package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrontEndService {
    public static void main(String[] args) throws IOException {
        // Define the port number the server will listen on.

        int port = 9000;
        // Create a ServerSocket object and bind it to the specified port number.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Output a message indicating that the server is listening on the specified
            // port.
            System.out.println("Server started, listening on port " + port);
            // Create an ExecutorService object to manage a pool of threads.
            ExecutorService executorService = Executors.newCachedThreadPool();
            // Enter an infinite loop to wait for incoming client connections.
            while (true) {
                // Accept an incoming client connection and create a new Socket object.
                Socket clientSocket = serverSocket.accept();
                // Output a message indicating that a new message has been received.
                System.out.println("New message received");
                // Create a new HttpServiceProcessor thread and hand it over to the
                // ExecutorService to manage.
                executorService.execute(new HttpServiceProcessor(clientSocket));
            }
        }
    }

}
