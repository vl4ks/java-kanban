package taskmanagement.taskmanager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedTaskHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();

            try {
                if ("GET".equals(method)) {
                    handleGet(exchange);
                } else {
                    System.out.println("Неправильный метод");
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                handleException(exchange, e);
            }
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> prioritized = manager.getPrioritizedTasks();
        sendResponse(exchange, gson.toJson(prioritized), 200);
    }
}
