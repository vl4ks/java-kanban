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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasksFromManager.get(0).getTitle(), "Некорректное имя подзадачи");
    }
    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        manager.createSubtask(subtask);
        subtask.setTitle("Updated Subtask 1");

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task updatedSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals("Updated Subtask 1", updatedSubtask.getTitle(), "Подзадача не обновлена");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getId(), subtaskFromResponse.getId(), "Неверный id подзадачи");
        assertEquals(subtask.getTitle(), subtaskFromResponse.getTitle(), "Неверное имя подзадачи");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        manager.createSubtask(subtask);

        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5));
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
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
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        assertThrows(NotFoundException.class, () -> manager.getTaskById(subtask.getId()), "Подзадача не была удалена");
    }
}