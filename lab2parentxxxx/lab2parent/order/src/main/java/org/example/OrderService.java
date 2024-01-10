package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderService {
    /**
     * The main method that starts the order service and listens for incoming client
     * connections on a specified port.
     *
     * @param args The command line arguments.
     * @throws IOException If an I/O error occurs while creating the server socket.
     */
    public static void main(String[] args) throws IOException {
        int port = 9002;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, listening on port " + port);
            ExecutorService executorService = Executors.newCachedThreadPool();
            // Listen for incoming client connections and process them using the
            // PosterProcessor class
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new HttpServiceProcessor(clientSocket));
            }
        }
    }
}
