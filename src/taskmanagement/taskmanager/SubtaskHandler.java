package taskmanagement.taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            try {
                if ("GET".equals(method)) {
                    handleGetSubtask(exchange, path);
                } else if ("POST".equals(method)) {
                    handlePostSubtask(exchange, path);
                } else if ("DELETE".equals(method)) {
                    handleDeleteSubtask(exchange, path);
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


    private void handleGetSubtask(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/subtasks")) {
            List<Subtask> subtasks = manager.getAllSubtasks();
            sendResponse(exchange, gson.toJson(subtasks), 200);
        } else if (path.matches("/subtasks/\\d+")) {
            int id = parseIdFromPath(path);
            Task subtask = manager.getSubtaskById(id);
            if (subtask == null) {
                sendNotFound(exchange);
            } else {
                sendResponse(exchange, gson.toJson(subtask), 200);
            }
        }
    }

    private void handlePostSubtask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks(/\\d+)?")) {
            try {
                String body = readRequestBody(exchange);
                Subtask subtask = gson.fromJson(body, Subtask.class);

                if (subtask == null) {
                    sendResponse(exchange, "Некорректный JSON формат", 400);
                    return;
                }

                if (subtask.getStartTime() == null) {
                    subtask.setStartTime(LocalDateTime.now());
                }
                if (subtask.getDuration() == null) {
                    subtask.setDuration(Duration.ZERO);
                }

                if (path.matches("/subtasks/\\d+")) { // Обработка пути /subtasks/{id}
                    int subtaskId = parseIdFromPath(path);

                    if (subtask.getId() != subtaskId) {
                        sendResponse(exchange, gson.toJson(subtask), 400);
                        return;
                    }

                    try {
                        manager.updateTask(subtask);
                        sendResponse(exchange, "Подзадача обновлена", 200);
                    } catch (NotFoundException e) {
                        sendResponse(exchange, "Подзадача с таким id не найдена", 404);
                    }
                } else {
                    if (subtask.getId() != 0) {
                        sendResponse(exchange, "Неверный id(для новой подзадачи id=0)", 400);
                        return;
                    }

                    manager.createSubtask(subtask);
                    sendResponse(exchange, "Подзадача создана", 201);
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


    private void handleDeleteSubtask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            int id = parseIdFromPath(path);
            if (id != -1) {
                manager.deleteTaskById(id);
                sendResponse(exchange, "Подзадача удалена ", 204);
            } else {
                sendResponse(exchange, "Неверный id ", 400);
            }
        } else if (path.equals("/subtasks")) {
            manager.removeAllSubtasks();
            sendResponse(exchange, "Сабтаски удалены", 200);
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
