import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File saveFile = new File("tasks.csv");

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return taskManager = createTaskManager();
    }

    @BeforeEach
    void setUp() {
        try {
            taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), saveFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Сохранение и загрузка пустого файла
    @Test
    void testSaveAndLoadEmptyFile() {
        assertTrue(saveFile.exists(), "Файл не был создан");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);
        System.out.println(loadedManager.getAllTasks());
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
}