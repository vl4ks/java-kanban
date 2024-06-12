import org.junit.jupiter.api.Test;
import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.FileBackedTaskManager;
import taskmanagement.taskmanager.InMemoryHistoryManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File saveFile;

    {
        try {
            saveFile = File.createTempFile("task_manager_test", ".csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager(), saveFile);
    }

    //Сохранение и загрузка пустого файла
    @Test
    void testSaveAndLoadEmptyFile() {
        assertTrue(saveFile.exists(), "Файл не был создан");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач не пуст");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков не пуст");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач не пуст");
    }

    //Сохранение и загрузка нескольких задач
    @Test
    void testSaveAndLoadTasks() {
        Task task = new Task("Задача файлового менеджера", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Epic epic = new Epic("Эпик файлового менеджера", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        Subtask subtask = new Subtask("Подзадача файлового менеджера", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().plusHours(1));

        taskManager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertEquals(taskManager.getAllTasks(), loadedManager.getAllTasks(), "Списки задач не совпадают");
        assertEquals(taskManager.getAllEpics(), loadedManager.getAllEpics(), "Списки эпиков не совпадают");
        assertEquals(taskManager.getAllSubtasks(), loadedManager.getAllSubtasks(), "Списки подзадач не совпадают");
    }

    @Test
    void testSaveAndLoadPrioritizedTasks() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 11, 9, 0));
        Task task2 = new Task("Задача 2", "Описание", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 11, 10, 0));
        Epic epic = new Epic("Эпик 1", "Описание", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 6, 11, 8, 0));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 6, 11, 11, 0));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        List<Task> prioritizedTasks = loadedManager.getPrioritizedTasks();

        assertEquals(subtask1, prioritizedTasks.get(0), "Подзадача 1 должна быть первой");
        assertEquals(task1, prioritizedTasks.get(1), "Задача 1 должна быть второй");
        assertEquals(task2, prioritizedTasks.get(2), "Задача 2 должна быть третьей");
        assertEquals(subtask2, prioritizedTasks.get(3), "Подзадача 2 должна быть четвертой");
    }
}