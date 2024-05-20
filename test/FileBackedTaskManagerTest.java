import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanagement.task.*;
import taskmanagement.taskmanager.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static File saveFile;
    private static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void setUp() {
        try {
            saveFile = File.createTempFile("task_manager_test", ".csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), saveFile);

        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());

        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertEquals(fileBackedTaskManager.getAllTasks(), loadedManager.getAllTasks(), "Списки задач не совпадают");
        assertEquals(fileBackedTaskManager.getAllEpics(), loadedManager.getAllEpics(), "Списки эпиков не совпадают");
        assertEquals(fileBackedTaskManager.getAllSubtasks(), loadedManager.getAllSubtasks(), "Списки подзадач не совпадают");
    }
}