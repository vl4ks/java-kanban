package taskmanagement.taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.task.Task;

import java.io.IOException;
import java.util.List;

class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            System.out.println("Received request: " + method + " " + path);

            try {
                if ("GET".equals(method)) {
                    handleGetTask(exchange, path);
                } else if ("POST".equals(method)) {
                    handlePostTask(exchange, path);
                } else if ("DELETE".equals(method)) {
                    handleDeleteTask(exchange, path);
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


    private void handleGetTask(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            List<Task> tasks = manager.getAllTasks();
            sendResponse(exchange, gson.toJson(tasks), 200);
        } else if (path.matches("/tasks/\\d+")) {
            int id = parseIdFromPath(path);
            Task task = manager.getTaskById(id);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                sendResponse(exchange, gson.toJson(task), 200);
            }
        }
    }

    private void handlePostTask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/tasks(/\\d+)?")) {
            try {
                String body = readRequestBody(exchange);
                Task task = gson.fromJson(body, Task.class);

                if (task == null) {
                    sendResponse(exchange, "Некорректный JSON формат", 400);
                    return;
                }

                if (path.matches("/tasks/\\d+")) { // Обработка пути /tasks/{id}
                    int taskId = parseIdFromPath(path);

                    if (task.getId() != taskId) {
                        sendResponse(exchange, "Неверный id", 400);
                        return;
                    }

                    try {
                        manager.getTaskById(taskId);
                        manager.updateTask(task);
                        sendResponse(exchange, "Задача обновлена", 200);
                    } catch (NotFoundException e) {
                        sendResponse(exchange, "Задача с таким id не найдена", 404);
                    }
                } else {
                    if (task.getId() != 0) {
                        sendResponse(exchange, "Неверный id(для новой задачи id=0)", 400);
                        return;
                    }

                    manager.createTask(task);
                    sendResponse(exchange, "Задача создана", 201);
                }
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, "Некорректный JSON формат", 400);
            } catch (Exception e) {
                handleException(exchange, e);
            }
        } else {
            sendResponse(exchange, "Эндпоинт не найден", 404);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/tasks/\\d+")) {
            int id = parseIdFromPath(path);
            if (id != -1) {
                manager.deleteTaskById(id);
                sendResponse(exchange, "Задача удалена ", 204);
            } else {
                sendResponse(exchange, "Неверный id ", 400);
            }
        } else {
            sendResponse(exchange, "Эндпоинт не найден", 404);
        }
    }

    private int parseIdFromPath(String path) {
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
