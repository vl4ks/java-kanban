package taskmanagement.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private TaskManager manager;
    private HttpServer server;

    private Gson gson;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.gson = getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        initContexts();
    }

    private void initContexts() {
        server.createContext("/tasks", new TaskHandler(manager, gson));
        server.createContext("/subtasks", new SubtaskHandler(manager, gson));
        server.createContext("/epics", new EpicHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedTaskHandler(manager, gson));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Сервер запущен на порту: " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен на порту: " + PORT);
        server.stop(0);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}
