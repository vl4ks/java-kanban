package taskmanagement.taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try {
            if ("GET".equals(method)) {
                handleGetEpic(exchange, path);
            } else if ("POST".equals(method)) {
                handlePostEpic(exchange, path);
            } else if ("DELETE".equals(method)) {
                handleDeleteEpic(exchange, path);
            } else {
                System.out.println("Неправильный метод");
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleException(exchange, e);
        }
    }

    private void handleGetEpic(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            List<Epic> epics = manager.getAllEpics();
            sendResponse(exchange, gson.toJson(epics), 200);
        } else if (path.matches("/epics/\\d+")) {
            int id = parseIdFromPath(path);
            if (id == -1) {
                sendResponse(exchange, "Некорректный ID эпика", 400);
                return;
            }
            Task epic = manager.getEpicById(id);
            if (epic != null) {
                sendResponse(exchange, gson.toJson(epic), 200);
            } else {
                sendNotFound(exchange);
            }
        } else if (path.matches("/epics/\\d+/subtasks")) {
            int id = parseIdFromPath(path);
            if (id == -1) {
                sendResponse(exchange, "Некорректный ID эпика", 400);
                return;
            }
            List<Subtask> subtasks = manager.getSubtasksByEpicId(id);
            if (subtasks != null) {
                sendResponse(exchange, gson.toJson(subtasks), 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendResponse(exchange, "Эндпоинт не найден", 404);
        }
    }

    private void handlePostEpic(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics")) {
            try {
                String body = readRequestBody(exchange);
                Epic epic = gson.fromJson(body, Epic.class);

                if (epic == null) {
                    sendResponse(exchange, "Некорректный JSON формат", 400);
                    return;
                }

                try {
                    Task existingEpic = manager.getEpicById(epic.getId());
                    if (existingEpic != null) {
                        sendResponse(exchange, gson.toJson(existingEpic), 406);
                        return;
                    }
                } catch (NotFoundException ignored) {
                    manager.createEpic(epic);
                    sendResponse(exchange, "Эпик создан", 201);
                }

            } catch (Exception e) {
                handleException(exchange, e);
            } finally {
                exchange.close();
            }
        } else {
            sendResponse(exchange, "Эндпоинт не найден", 404);
            exchange.close();
        }
    }


    private void handleDeleteEpic(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            int id = parseIdFromPath(path);
            if (manager.getEpicById(id) != null) {
                manager.deleteTaskById(id);
                sendResponse(exchange, "Эпик удален ", 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendResponse(exchange, "Эндпоинт не найден", 404);
        }

    }

    private int parseIdFromPath(String path) {
        try {
            String[] segments = path.split("/");
            //поиск последнего числового значения в пути
            for (int i = segments.length - 1; i >= 0; i--) {
                try {
                    return Integer.parseInt(segments[i]);
                } catch (NumberFormatException ignored) {
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
