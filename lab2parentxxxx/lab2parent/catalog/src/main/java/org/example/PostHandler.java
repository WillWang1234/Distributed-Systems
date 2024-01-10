package org.example;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PostHandler implements Runnable {
    private Socket clientSocket;
    String requestHead;
    // Constructor to initialize class variables
    String body;

    public PostHandler(Socket clientSocket, String request, String body) {
        this.clientSocket = clientSocket;
        this.requestHead = request;
        this.body = body;
    }

    // Method to handle the POST request
    @Override
    public void run() {
        try (OutputStream outputStream = clientSocket.getOutputStream()) {
            String[] requestParts = requestHead.split(" ");
            FileHelper fileHelper = new FileHelper();
            if ("POST".equals(requestParts[0])) {
                String path = requestParts[1];
                // Check if the path is "/order"
                if (path.equals("/order")) {
                    String requestBody = body;
                    System.out.println("Request Body: " + requestBody);
                    JSONObject order = new JSONObject(requestBody);

                    String Name = order.optString("Name");
                    // Check if the stock exists in the catalog
                    if (!CatalogService.stocksCatalog.containsKey(Name)) {
                        JSONObject errorResponse = new JSONObject();
                        errorResponse.put("error", new JSONObject()
                                .put("code", 404)
                                .put("message", "Stock not found"));
                        sendJsonResponse(outputStream, 404, errorResponse);
                    }

                    Stock stock = CatalogService.stocksCatalog.get(Name);
                    int quantity = order.optInt("quantity");
                    String type = order.optString("type");
                    if ("buy".equals(type)) {
                        // Check if the stock has enough quantity
                        if (stock.getQuantity() < quantity) {
                            JSONObject errorResponse = new JSONObject();
                            errorResponse.put("error", new JSONObject()
                                    .put("code", 400)
                                    .put("message", "Quantity exceeds inventory"));
                            sendJsonResponse(outputStream, 400, errorResponse);
                        }
                        // Update the trades and stocks catalogs
                        CatalogService.tradeNumber++;
                        fileHelper.updateTrades(
                                new Trade(CatalogService.tradeNumber, Name, type, quantity));
                        fileHelper.updateStocks(Name, type, quantity);

                        JSONObject response = new JSONObject();
                        response.put("data", new JSONObject().put("Order Number", CatalogService.tradeNumber));
                        sendJsonResponse(outputStream, 200, response);
                    } else if ("sell".equals(type)) {
                        CatalogService.tradeNumber++;
                        fileHelper.updateTrades(
                                new Trade(CatalogService.tradeNumber, Name, type, quantity));
                        fileHelper.updateStocks(Name, type, quantity);

                        JSONObject response = new JSONObject();
                        response.put("data", new JSONObject().put("Order Number", CatalogService.tradeNumber));
                        sendJsonResponse(outputStream, 200, response);
                    } else {
                        JSONObject errorResponse = new JSONObject();
                        errorResponse.put("error", new JSONObject()
                                .put("code", 400)
                                .put("message", "Invalid trade type"));
                        sendJsonResponse(outputStream, 400, errorResponse);
                    }
                } else {
                    sendResponseHeader(outputStream, 404, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Sends a JSON response with the given status code and content.
    private void sendJsonResponse(OutputStream outputStream, int statusCode, JSONObject jsonResponse)
            throws IOException {
        byte[] responseBytes = jsonResponse.toString().getBytes(StandardCharsets.UTF_8);
        sendResponseHeader(outputStream, statusCode, responseBytes.length);
        outputStream.write(responseBytes);
        outputStream.flush();
    }

    // Sends an HTTP response header with the given status code and content length.
    private void sendResponseHeader(OutputStream outputStream, int statusCode, int contentLength) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\r\n");
        writer.write("Content-Type: application/json; charset=UTF-8\r\n");
        writer.write("Content-Length: " + contentLength + "\r\n");
        writer.write("\r\n");
        writer.flush();
    }

    // Gets the HTTP status message for the given status code.
    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            default:
                return "Unknown";
        }
    }
}
