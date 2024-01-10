package org.example;

//**import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**This aspect of code are the old version.
 * 
 * This class represents an HTTP tool for performing stock lookups and placing
 * orders.
 */
// public class HttpTool {
//     public String[] stocks = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };

//     /**
//      * 
//      * Starts an HTTP session with the specified lookup URL, order URL, and
//      * probability.
//      * 
//      * @param lookupUrl   The URL to use for stock lookups.
//      * @param orderUrl    The URL to use for placing orders.
//      * @param probability The probability of placing an order for a given stock
//      *                    lookup.
//      */
//     public void start(String lookupUrl, String orderUrl, double probability) {
//         System.out.println("dsa");
//         try {
//             HttpClient httpClient = HttpClient.newHttpClient();
//             for (int i = 0; i < 10; i++) {
//                 System.out.println("dsa");
//                 // Choose a random stock from the array
//                 int randomIndex = (int) (Math.random() * stocks.length);
//                 String stockName = stocks[randomIndex];
//                 String currentLookupUrl = lookupUrl + stockName;
//                 HttpRequest lookupRequest = HttpRequest.newBuilder()
//                         .uri(URI.create(currentLookupUrl))
//                         .GET()
//                         .build();
//                 // Send the lookup request and process the response
//                 HttpResponse<String> lookupResponse = httpClient.send(lookupRequest,
//                         HttpResponse.BodyHandlers.ofString());
//                 System.out.println("Complete response: " + lookupResponse.toString());

//                 // HttpResponse<String> lookupResponse = httpClient.send(lookupRequest,
//                 // HttpResponse.BodyHandlers.ofString());
//                 JSONObject lookupJson = new JSONObject(lookupResponse.body());

//                 System.out.println("Response: " + lookupJson.toString());
//                 if (lookupJson != null && lookupJson.has("data")) {
//                     JSONObject data = lookupJson.getJSONObject("data");
//                     if (data.has("Quantity") && data.getInt("Quantity") > 0 && Math.random() <= probability) {
//                         sendOrderRequest(httpClient, orderUrl, stockName);
//                     }
//                 }

//                 // try {
//                 // Thread.sleep(1000); // Add a delay of 1 second (1000 milliseconds) between
//                 // requests
//                 // } catch (InterruptedException e) {
//                 // e.printStackTrace();
//                 // }
//             }

//         } catch (IOException | InterruptedException e) {
//             e.printStackTrace();
//             System.out.println("error");
//         }
//     }

//     /**
//      * 
//      * Sends an HTTP POST request to the specified order URL with the specified
//      * order data.
//      * 
//      * @param httpClient The HTTP client to use for sending the request.
//      * @param orderUrl   The URL to use for placing the order.
//      * @param stockName  The name of the stock to place an order for.
//      * @throws IOException          If an I/O error occurs while sending the
//      *                              request.
//      * @throws InterruptedException If the thread is interrupted while waiting for
//      *                              the response.
//      */
//     public void sendOrderRequest(HttpClient httpClient, String orderUrl, String stockName)
//             throws IOException, InterruptedException {
//         JSONObject orderData = new JSONObject();
//         orderData.put("type", "buy");
//         orderData.put("stockName", stockName);
//         orderData.put("quantity", 10);
//         // Build the order request with the order data
//         HttpRequest orderRequest = HttpRequest.newBuilder()
//                 .uri(URI.create(orderUrl))
//                 .POST(HttpRequest.BodyPublishers.ofString(orderData.toString()))
//                 .header("Content-Type", "application/json; charset=UTF-8")
//                 .build();

//         HttpResponse<String> tradeResponse = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());
//         // Send the order request and process the response
//         if (tradeResponse != null) {
//             System.out.println("Trade response: " + tradeResponse.body());
//         }
//     }
// }

// import java.time.Duration;

// import org.json.JSONObject;

// public class HttpTool {
//     public String[] stocks = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };

//     public void start(String lookupUrl, String orderUrl, double probability) {
//         System.out.println("Starting...");
//         HttpClient httpClient = HttpClient.newBuilder()
//                 .connectTimeout(Duration.ofSeconds(5))
//                 .build();
//         for (int i = 0; i < 10; i++) {
//             System.out.println("Iteration: " + (i + 1));
//             int randomIndex = (int) (Math.random() * stocks.length);
//             String stockName = stocks[randomIndex];
//             String currentLookupUrl = lookupUrl + stockName;
//             HttpRequest lookupRequest = HttpRequest.newBuilder()
//                     .uri(URI.create(currentLookupUrl))
//                     .GET()
//                     .build();
//             try {
//                 HttpResponse<String> lookupResponse = httpClient.send(lookupRequest,
//                         HttpResponse.BodyHandlers.ofString());
//                 System.out.println("Complete response: " + lookupResponse.toString());

//                 JSONObject lookupJson = new JSONObject(lookupResponse.body());
//                 System.out.println("Response: " + lookupJson.toString());
//                 if (lookupJson != null && lookupJson.has("data")) {
//                     JSONObject data = lookupJson.getJSONObject("data");
//                     if (data.has("Quantity") && data.getInt("Quantity") > 0 && Math.random() <= probability) {
//                         sendOrderRequest(httpClient, orderUrl, stockName);
//                     }
//                 }
//             } catch (IOException | InterruptedException e) {
//                 e.printStackTrace();
//                 System.out.println("Error occurred during request for: " + stockName);
//             }

//             try {
//                 Thread.sleep(1000); // Add a delay of 1 second (1000 milliseconds) between requests
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//     }

//     public void sendOrderRequest(HttpClient httpClient, String orderUrl, String stockName)
//             throws IOException, InterruptedException {
//         JSONObject orderData = new JSONObject();
//         orderData.put("type", "buy");
//         orderData.put("stockName", stockName);
//         orderData.put("quantity", 10);
//         HttpRequest orderRequest = HttpRequest.newBuilder()
//                 .uri(URI.create(orderUrl))
//                 .POST(HttpRequest.BodyPublishers.ofString(orderData.toString()))
//                 .header("Content-Type", "application/json; charset=UTF-8")
//                 .build();

//         HttpResponse<String> tradeResponse = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());
//         if (tradeResponse != null) {
//             System.out.println("Trade response: " + tradeResponse.body());
//         }
//     }
// }

// import java.time.Duration;

// import org.json.JSONObject;

// public class HttpTool {
//     public String[] stocks = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };

//     // start the httptool
//     public void start(String lookupUrl, String orderUrl, double probability) {
//         System.out.println("Starting...");
//         HttpClient httpClient = HttpClient.newBuilder()
//                 .connectTimeout(Duration.ofSeconds(5))
//                 .build();
//         for (int i = 0; i < 10; i++) {
//             System.out.println("Iteration: " + (i + 1));
//             // Select a random stock name to look up
//             int randomIndex = (int) (Math.random() * stocks.length);
//             String stockName = stocks[randomIndex];
//             String currentLookupUrl = lookupUrl + stockName;
//             System.out.println("Looking up: " + stockName);
//             HttpRequest lookupRequest = HttpRequest.newBuilder()
//                     .uri(URI.create(currentLookupUrl))
//                     .GET()
//                     .build();
//             try {
//                 // Send the request and get the response
//                 HttpResponse<String> lookupResponse = httpClient.send(lookupRequest,
//                         HttpResponse.BodyHandlers.ofString());
//                 System.out.println("Complete response: " + lookupResponse.toString());

//                 JSONObject lookupJson = new JSONObject(lookupResponse.body());
//                 System.out.println("Response: " + lookupJson.toString());
//                 // If the stock is available and the probability is met, send a POST request to
//                 // place an order
//                 if (lookupJson != null && lookupJson.has("data")) {
//                     JSONObject data = lookupJson.getJSONObject("data");
//                     if (data.has("Quantity") && data.getInt("Quantity") > 0 && Math.random() <= probability) {
//                         sendOrderRequest(httpClient, orderUrl, stockName, "sell");
//                         sendOrderRequest(httpClient, orderUrl, stockName, "buy");
//                     }
//                 }
//             } catch (IOException | InterruptedException e) {
//                 e.printStackTrace();
//                 System.out.println("Error occurred during request for: " + stockName);
//             }
//         }
//     }

//     // Send a POST request to place an order for a stock.
//     public void sendOrderRequest(HttpClient httpClient, String orderUrl, String stockName, String type)
//             throws IOException, InterruptedException {
//         // Create a JSON object containing order information

//         JSONObject orderData = new JSONObject();
//         orderData.put("type", type);
//         orderData.put("Name", stockName);
//         orderData.put("quantity", 10);
//         // Create a POST request to place the order
//         HttpRequest orderRequest = HttpRequest.newBuilder()
//                 .uri(URI.create(orderUrl))
//                 .POST(HttpRequest.BodyPublishers.ofString(orderData.toString()))
//                 .header("Content-Type", "application/json; charset=UTF-8")
//                 .build();
//         // Send the request and get the response
//         HttpResponse<String> tradeResponse = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());
//         if (tradeResponse != null) {
//             System.out.println("Trade response: " + tradeResponse.body());
//         }
//     }

// }

import java.time.Duration;

import org.json.JSONObject;

public class HttpTool {
    public String[] stocks = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };

    public void start(String lookupUrl, String orderUrl, double probability) {
        System.out.println("Starting...");
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        while (true) {
            // System.out.println("Iteration: " + (i + 1));
            int randomIndex = (int) (Math.random() * stocks.length);
            String stockName = stocks[randomIndex];
            String currentLookupUrl = lookupUrl + stockName;
            System.out.println("Looking up: " + stockName);
            HttpRequest lookupRequest = HttpRequest.newBuilder()
                    .uri(URI.create(currentLookupUrl))
                    .GET()
                    .build();

            long startTime = System.currentTimeMillis();
            try {
                HttpResponse<String> lookupResponse = httpClient.send(lookupRequest,
                        HttpResponse.BodyHandlers.ofString());
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                System.out.println("Lookup request latency: " + latency + " ms");

                JSONObject lookupJson = new JSONObject(lookupResponse.body());
                System.out.println("Response: " + lookupJson.toString());
                if (lookupJson != null && lookupJson.has("data")) {
                    JSONObject data = lookupJson.getJSONObject("data");
                    if (data.has("Quantity") && data.getInt("Quantity") > 0 && Math.random() <= probability) {
                        sendOrderRequest(httpClient, orderUrl, stockName, "sell");
                        sendOrderRequest(httpClient, orderUrl, stockName, "buy");
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error occurred during request for: " + stockName);
            }
        }
    }

    public void sendOrderRequest(HttpClient httpClient, String orderUrl, String stockName, String type)
            throws IOException, InterruptedException {
        JSONObject orderData = new JSONObject();
        orderData.put("type", type);
        orderData.put("Name", stockName);
        orderData.put("quantity", 10);

        HttpRequest orderRequest = HttpRequest.newBuilder()
                .uri(URI.create(orderUrl))
                .POST(HttpRequest.BodyPublishers.ofString(orderData.toString()))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        long startTime = System.currentTimeMillis();
        HttpResponse<String> tradeResponse = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        System.out.println("Trade request latency: " + latency + " ms");

        if (tradeResponse != null) {
            System.out.println("Trade response: " + tradeResponse.body());
        }
    }
}
