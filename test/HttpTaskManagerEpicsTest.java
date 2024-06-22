import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .build();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            assertEquals(201, response.statusCode());
        } catch (ConnectException e) {
            e.printStackTrace();
            fail("Сервер недоступен: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Запрос завершился неудачей: " + e.getMessage());
        }
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getId(), epicFromResponse.getId(), "Неверный id эпика");
        assertEquals(epic.getTitle(), epicFromResponse.getTitle(), "Неверное имя эпика");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", TaskStatus.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] epicsFromResponse = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epicsFromResponse.length, "Неверное количество эпиков");
    }

    @Test
    public void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask[] subtasksFromResponse = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, subtasksFromResponse.length, "Неверное количество подзадач");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
