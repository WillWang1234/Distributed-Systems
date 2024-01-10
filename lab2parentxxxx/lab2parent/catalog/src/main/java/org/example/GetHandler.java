package org.example;

import org.json.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class GetHandler implements Runnable{
    Socket socket;
    String request;
    GetHandler(Socket socket, String request){
        this.socket = socket;
        this.request = request;
    }
    @Override
    public void run(){
        try{

            if (request != null) {
                String[] requestParts = request.split(" ");
                String requestMethod = requestParts[0];
                String requestPath = requestParts[1];

                if ("GET".equalsIgnoreCase(requestMethod)) {
                    handleGetRequest(socket, requestPath);
                } else {
                    sendErrorResponse(socket, 405, "Method Not Allowed");
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void handleGetRequest(Socket socket, String path) throws IOException {
        if (path.startsWith("/stocks/")) {
            String stockName = path.substring(8);
            try {
                JSONObject jsonObject = new JSONObject();

                if (CatalogService.stocksCatalog.containsKey(stockName)) {
                    Stock s1 = CatalogService.stocksCatalog.get(stockName);
                    JSONObject response = new JSONObject();
                    JSONObject responseData = new JSONObject();
                    responseData.put("Name", s1.getStockName());
                    responseData.put("Price", s1.getPrice());
                    responseData.put("Quantity", s1.getQuantity());
                    response.put("data", responseData);
                    sendJsonResponse(socket, response, 200);
                } else {
                    sendErrorResponse(socket, 404, "Stock not found");
                }

            } catch (Exception e) {
                sendErrorResponse(socket, 500, e.getMessage());
            }finally {
                // Close the socket in the finally block to ensure proper cleanup.
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        } else {
            sendErrorResponse(socket, 404, "Not Found");
        }
    }

    private static void sendJsonResponse(Socket socket, JSONObject jsonResponse, int statusCode) throws IOException {
        byte[] responseBytes = jsonResponse.toString().getBytes(StandardCharsets.UTF_8);
        String statusMessage = getStatusMessage(statusCode);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        out.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        out.println("Content-Type: application/json; charset=UTF-8");
        out.println("Content-Length: " + responseBytes.length);
        out.println("");
        out.flush();

        OutputStream os = socket.getOutputStream();
        os.write(responseBytes);
        os.flush();
    }

    private static void sendErrorResponse(Socket socket, int statusCode, String message) throws IOException {
        JSONObject errorData = new JSONObject();
        errorData.put("code", statusCode);
        errorData.put("message", message);
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", errorData);
        sendJsonResponse(socket, errorResponse, statusCode);
    }

    private static String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 405:
                return "Method Not Allowed";
            default:
                return "Unknown";
        }
    }
}