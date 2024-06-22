package taskmanagement.taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanagement.task.ErrorResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

abstract class BaseHttpHandler implements HttpHandler {

    protected final Gson gson;

    public BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        }

        long contentLength = (statusCode == 204 || responseBytes.length == 0) ? -1 : responseBytes.length;

        try {
            exchange.sendResponseHeaders(statusCode, contentLength);

            if (contentLength > 0) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }


    protected String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\":\"Not Found\"}", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\":\"Conflict\"}", 406);
    }

    protected void handleException(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace();

        try {
            if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
                if (e instanceof ManagerIOException) {
                    sendError(exchange, 500, "Внутренняя ошибка сервера: " + e.getMessage());
                } else if (e instanceof NotFoundException) {
                    sendNotFound(exchange);
                } else if (e instanceof ValidationException) {
                    sendHasInteractions(exchange);
                } else if (e instanceof JsonSyntaxException) {
                    sendError(exchange, 400, "Получен некорректный JSON: " + e.getMessage());
                } else {
                    sendError(exchange, 500, "Произошла непредвиденная ошибка: " + e.getMessage());
                }
            } else {
                System.err.println("Ошибка обработки запроса после отправки заголовков: " + e.getMessage());
            }
        } catch (Exception innerException) {
            innerException.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        if (exchange.getResponseHeaders().containsKey("Content-Type")) {
            return;
        }
        ErrorResponse errorResponse = new ErrorResponse(message);
        String responseJson = gson.toJson(errorResponse);
        sendResponse(exchange, responseJson, statusCode);
    }
}