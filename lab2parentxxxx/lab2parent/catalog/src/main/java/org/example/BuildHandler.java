package org.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import org.json.*;

//runable class to handle client connections and HTTP get requests
public class BuildHandler implements Runnable {
    private Socket clientSocket;

    public BuildHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * 
     * Handles the client connection by reading the request, parsing it, and
     * handling the GET request.
     * 
     * @throws IOException if there is an error in the input or output streams
     */
    @Override
    public void run() {
        try {
            handleClientConnection(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * Reads the request and handles the GET request based on the path.
     * 
     * @param socket the client socket to handle
     * 
     * @throws IOException if there is an error in the input or output streams
     */
    private void handleClientConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String requestLine = in.readLine();
        String[] requestParts = requestLine.split(" ");

        if ("GET".equalsIgnoreCase(requestParts[0])) {
            handleGetRequest(requestParts[1], out);
        }

        in.close();
        out.close();
        socket.close();
    }

    // handle the incoming Get requests
    private void handleGetRequest(String path, DataOutputStream out) throws IOException {
        System.out.println("Handling GET request for path: " + path);
        if (path.startsWith("/stocks/")) {
            String stockName = path.substring(8);
            try {
                System.out.println("New message received");
                if (CatalogService.stocksCatalog.containsKey(stockName)) {
                    Stock s1 = CatalogService.stocksCatalog.get(stockName);

                    JSONObject response = new JSONObject();
                    JSONObject responseData = new JSONObject();
                    responseData.put("Name", s1.getStockName());
                    responseData.put("Price", s1.getPrice());
                    responseData.put("Quantity", s1.getQuantity());
                    response.put("data", responseData);
                    sendJsonResponse(out, response, 200);
                } else {
                    JSONObject errorData = new JSONObject();
                    errorData.put("code", 404);
                    errorData.put("message", "stock not found");
                    JSONObject errorResponse = new JSONObject();
                    errorResponse.put("error", errorData);
                    sendJsonResponse(out, errorResponse, 404);
                }

            } catch (Exception e) {
                JSONObject errorData = new JSONObject();
                errorData.put("code", 500);
                errorData.put("message", e.getMessage());
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", errorData);
                sendJsonResponse(out, errorResponse, 500);
            }
        } else {
            JSONObject errorData = new JSONObject();
            errorData.put("code", 404);
            errorData.put("message", "Invalid URL");
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", errorData);
            sendJsonResponse(out, errorResponse, 500);
        }
    }

    // send a json response to the client.
    private void sendJsonResponse(DataOutputStream out, JSONObject jsonResponse, int statusCode) throws IOException {
        byte[] responseBytes = jsonResponse.toString().getBytes("UTF-8");
        out.writeBytes("HTTP/1.1 " + statusCode + " OK\r\n");
        out.writeBytes("Content-Type: application/json; charset=UTF-8\r\n");
        out.writeBytes("Content-Length: " + responseBytes.length + "\r\n");
        out.writeBytes("\r\n");
        out.write(responseBytes);
        out.flush();
    }
}
