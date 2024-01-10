package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// public class CatalogService {
//     public static ConcurrentHashMap<String, Stock> stocksCatalog = new ConcurrentHashMap<>();
//     public static ConcurrentMap<Integer, Trade> tradeCatalog = new ConcurrentHashMap<>();
//     public static int tradeNumber = 1;

//     public static void main(String[] args) throws IOException {
//         // Initialize stockCatalog and transactionCatalog
//         initialCatalog();

//         // Create server and set executor
//         int port = 9001;
//         int numThreads = 50;
//         ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//         try (ServerSocket serverSocket = new ServerSocket(port)) {
//             System.out.println("CatalogServer started, listening on port " + port);

//             while (true) {

//                 Socket clientSocket = serverSocket.accept();
//                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                 String request = in.readLine();
//                 String[] requestParts = request.split(" ");
//                 if ("GET".equalsIgnoreCase(requestParts[0])) {
//                     executorService.submit(new BuildHandler(clientSocket));
//                 } else if ("POST".equalsIgnoreCase(requestParts[0])) {
//                     executorService.submit(new PostHandler(clientSocket));
//                 }
//             }
//         }
//     }

//     public static void initialCatalog() throws IOException {
//         FileHelper fileHelper = new FileHelper();
//         fileHelper.initializeCatalog();
//     }

//     // private static String processRequest(String request) {
//     // String[] requestParts = request.split(" ");
//     // if ("GET".equalsIgnoreCase(requestParts[0])) {
//     // String path = requestParts[1];

//     // if (path.startsWith("/stocks/")) {
//     // String stockName = path.substring(8);

//     // if (CatalogService.stocksCatalog.containsKey(stockName)) {
//     // Stock stock = CatalogService.stocksCatalog.get(stockName);
//     // return "Name: " + stock.getStockName() + ", Price: " + stock.getPrice() + ",
//     // Quantity: "
//     // + stock.getQuantity();
//     // } else {
//     // return "Stock not found";
//     // }
//     // } else {
//     // return "Invalid path";
//     // }
//     // } else if ("POST".equalsIgnoreCase(requestParts[0])) {
//     // String path = requestParts[1];

//     // if (path.startsWith("/orders")) {
//     // try {
//     // String requestBody = request.substring(request.indexOf("{"),
//     // request.indexOf("}") + 1);
//     // JSONObject order = new JSONObject(requestBody);

//     // String stockName = order.getString("stockName");
//     // String type = order.getString("type");
//     // int quantity = order.getInt("quantity");

//     // // Validate stock exists
//     // if (!CatalogService.stocksCatalog.containsKey(stockName)) {
//     // return "Stock not found";
//     // }

//     // // Validate enough quantity available for sell order
//     // if ("sell".equals(type) &&
//     // CatalogService.stocksCatalog.get(stockName).getQuantity() < quantity) {
//     // return "Not enough quantity available for sell order";
//     // }

//     // // Process order
//     // if ("buy".equals(type)) {
//     // int tradeId = CatalogService.tradeNumber++;
//     // Trade trade = new Trade(tradeNumber, stockName, type, quantity);
//     // CatalogService.tradeCatalog.put(tradeId, trade);
//     // return "Buy order processed: " + trade.toString();
//     // } else if ("sell".equals(type)) {
//     // int tradeId = CatalogService.tradeNumber++;
//     // Trade trade = new Trade(tradeNumber, stockName, type, quantity);
//     // CatalogService.tradeCatalog.put(tradeId, trade);
//     // return "Sell order processed: " + trade.toString();
//     // } else {
//     // return "Invalid order type";
//     // }
//     // } catch (JSONException e) {
//     // return "Invalid JSON request body";
//     // }
//     // } else {
//     // return "Invalid path";
//     // }
//     // } else {
//     // return "Invalid request method";
//     // }
//     // }
// }
public class CatalogService {
    public static ConcurrentHashMap<String, Stock> stocksCatalog = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Trade> tradeCatalog = new ConcurrentHashMap<>();
    public static int tradeNumber = 1;

    // The main method initializes the catalog and sets up a server to handle
    // incoming requests.
    public static void main(String[] args) throws IOException {
        // Initialize stockCatalog and transactionCatalog
        initialCatalog();

        // Create server and set executor
        int port = 9001;
        int numThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("CatalogServer started, listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                String request = "";

                int contentLength = 0;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    request += line + "\r\n";
                    if (line.toLowerCase().startsWith("content-length")) {
                        String[] headerParts = line.split(":\\s+", 2);
                        if (headerParts.length > 1) {
                            contentLength = Integer.parseInt(headerParts[1]);
                        }
                    }
                }
                char[] body = new char[contentLength];
                // read incoming request body
                if (contentLength > 0) {

                    in.read(body, 0, contentLength);
                    request += new String(body);
                }

                System.out.println("Request: " + request);
                String[] requestParts = request.split(" ");
                if ("GET".equalsIgnoreCase(requestParts[0])) {
                    executorService.submit(new GetHandler(clientSocket, request));
                } else if ("POST".equalsIgnoreCase(requestParts[0])) {
                    executorService.submit(new PostHandler(clientSocket, request, new String(body)));
                } else {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("HTTP/1.0 400 Bad Request");
                    out.println("Content-Type: text/html");
                    out.println("");
                    out.println("<html><head></head><body>400 Bad Request</body></html>");
                    out.flush();
                }
            }
        }
    }

    // Initializes the catalog by loading stock data from a file.
    public static void initialCatalog() throws IOException {
        FileHelper fileHelper = new FileHelper();
        fileHelper.initializeCatalog();
    }
}
