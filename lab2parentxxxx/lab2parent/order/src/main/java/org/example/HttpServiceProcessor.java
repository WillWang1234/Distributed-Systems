package org.example;

import java.io.*;
import java.net.*;
import org.json.*;

public class HttpServiceProcessor implements Runnable {
    private Socket socket;
    String request;

    // initialize the socket
    public HttpServiceProcessor(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // get the input and output streams of the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream output = socket.getOutputStream();
            // read incoming request data
            request = "";
            String line;
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

            String method = requestParts[0];
            String path = requestParts[1];

            if (method.equals("POST")) {
                handlePostRequest(output, path, new String(body));
            } else {
                sendErrorResponse(output, "Invalid method", 400);
            }
            // close the streams
            in.close();
            output.close();
        } catch (Exception e) {
            System.err.println("Error handling HTTP request: " + e.getMessage());
        } finally {
            // Close the socket in the finally block to ensure proper cleanup.
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    // Handle POST requests.
    private void handlePostRequest(OutputStream output, String path, String body) throws IOException {
        if (path.startsWith("/order")) {
            JSONObject tradeDetails;
            try {
                tradeDetails = new JSONObject(body);
            } catch (JSONException e) {
                sendErrorResponse(output, "Invalid JSON input: " + e.getMessage(), 400);
                return;
            }
            // Send a POST request to the order service and get the trade result
            URL url = new URL("http://localhost:9001/order");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(tradeDetails.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            JSONObject tradeResult = getJSON(connection);

            sendJsonResponse(output, tradeResult, connection.getResponseCode());

        } else {
            sendErrorResponse(output, "Invalid path", 404);
        }
    }

    /**
     *
     * Send a JSON response to the client.
     *
     * @param output       The output stream to write the response to.
     *
     * @param jsonResponse The JSON object representing the response.
     *
     * @param statusCode   The HTTP status code of the response.
     *
     * @throws IOException if an I/O error occurs while writing the response.
     */
    private void sendJsonResponse(OutputStream output, JSONObject jsonResponse, int statusCode) throws IOException {
        byte[] responseBytes = jsonResponse.toString().getBytes("UTF-8");
        String statusText;

        switch (statusCode) {
            case 200:
                statusText = "OK";
                break;
            case 201:
                statusText = "Created";
                break;
            case 404:
                statusText = "Not Found";
                break;
            case 500:
                statusText = "Internal Server Error";
                break;
            default:
                statusText = "OK";
        }

        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + responseBytes.length + "\r\n" + "\r\n";
        output.write(response.getBytes("UTF-8"));
        output.write(responseBytes);
    }

    /**
     *
     * Send an error response to the client.
     *
     * @param output     The output stream to write the response to.
     *
     * @param message    The error message to include in the response.
     *
     * @param statusCode The HTTP status code of the response.
     *
     * @throws IOException if an I/O error occurs while writing the response.
     */
    private void sendErrorResponse(OutputStream output, String message, int statusCode) throws IOException {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("code", statusCode);
        errorResponse.put("message", message);
        sendJsonResponse(output, errorResponse, statusCode);
    }

    /**
     *
     * Get a JSON object from an HTTP connection.
     *
     * @param connection The HTTP connection to get the JSON object from.
     *
     * @return The JSON object.
     *
     * @throws IOException   if an I/O error occurs while getting the JSON object.
     *
     * @throws JSONException if there is an error parsing the JSON response.
     */
    private JSONObject getJSON(HttpURLConnection connection) throws IOException, JSONException {
        int responseCode = connection.getResponseCode();
        BufferedReader in;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return new JSONObject(content.toString());
    }
}
