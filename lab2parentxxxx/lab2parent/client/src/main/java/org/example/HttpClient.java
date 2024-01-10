package org.example;

/**
 * 
 * This class represents a simple HTTP client for interacting with a server that
 * provides stock information
 * 
 * and allows the placement of stock orders.
 */
// public class HttpClient {
//     public static String[] stockNames = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };

//     public static void main(String[] args) {
//         // Adjustable probability parameter (0 <= p <= 1)
//         double p = 0.5;
//         // Start the session
//         HttpTool httptool = new HttpTool();
//         httptool.start("http://localhost:9000/stocks/", "http://localhost:9000/order", p);
//     }
// }
import java.util.ArrayList;
import java.util.List;

public class HttpClient {
    public static String[] stockNames = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };

    public static void main(String[] args) {
        // Adjustable probability parameter (0 <= p <= 1)
        double p = 0.5;
        int numberOfClients = 5;

        // Start the session
        List<Thread> clientThreads = new ArrayList<>();

        for (int i = 0; i < numberOfClients; i++) {
            Thread thread = new Thread(() -> {
                HttpTool httptool = new HttpTool();
                httptool.start("http://localhost:9000/stocks/", "http://localhost:9000/order", p);
            });
            clientThreads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : clientThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
