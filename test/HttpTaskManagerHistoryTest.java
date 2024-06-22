import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.HttpTaskServer;
import taskmanagement.taskmanager.InMemoryHistoryManager;
import taskmanagement.taskmanager.InMemoryTaskManager;
import taskmanagement.taskmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testHandleGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().minusDays(1));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");

        List<Task> history = gson.fromJson(response.body(), List.class);
        assertNotNull(history, "Ответ не содержит список задач");
        assertEquals(2, history.size(), "Некорректное количество задач в истории");

    }
}
