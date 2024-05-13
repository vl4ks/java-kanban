import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanagement.task.*;
import taskmanagement.taskmanager.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static File saveFile;
    private static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void setUp() {
        try {
            saveFile = File.createTempFile("task_manager_test", ".csv");
            fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), saveFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Сохранение и загрузка пустого файла
    @Test
    void testSaveAndLoadEmptyFile() {
        fileBackedTaskManager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    //Сохранение и загрузка нескольких задач
    @Test
    void testSaveAndLoadTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Задача 3", "Описание задачи 3", TaskStatus.DONE);

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.createTask(task3);

        fileBackedTaskManager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertEquals(3, loadedManager.getAllTasks().size());
        assertTrue(loadedManager.getAllTasks().contains(task1));
        assertTrue(loadedManager.getAllTasks().contains(task2));
        assertTrue(loadedManager.getAllTasks().contains(task3));
    }

    @Test
    void testCreateTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        assertEquals(1, fileBackedTaskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(task, fileBackedTaskManager.getTaskById(task.getId()), "Задачи не совпадают.");
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        fileBackedTaskManager.createEpic(epic);
        assertEquals(1, fileBackedTaskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(epic, fileBackedTaskManager.getEpicById(epic.getId()), "Эпики не совпадают.");
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        fileBackedTaskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);

        assertEquals(1, fileBackedTaskManager.getAllSubtasks().size(), "Неверное количество подзадач.");
        assertEquals(subtask, fileBackedTaskManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают.");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        fileBackedTaskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, fileBackedTaskManager.getTaskById(task.getId()).getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        assertNotNull(task, "Задача после создания не должна быть null");
        assertNotNull(task.getId(), "У задачи должен быть установлен ID");
        fileBackedTaskManager.deleteTaskById(task.getId());

        assertNull(fileBackedTaskManager.getTaskById(task.getId()), "Задача не удалена.");
    }

    @Test
    void testAddDifferentTaskTypes() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, 1);

        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);

        assertEquals(1, fileBackedTaskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(1, fileBackedTaskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(1, fileBackedTaskManager.getAllSubtasks().size(), "Неверное количество подзадач.");

        assertEquals(task, fileBackedTaskManager.getTaskById(task.getId()), "Задачи не совпадают.");
        assertEquals(epic, fileBackedTaskManager.getEpicById(epic.getId()), "Эпики не совпадают.");
        assertEquals(subtask, fileBackedTaskManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают.");
    }

    @Test
    void testAddNewTask() {
        Task task = new Task("Задача добавлена", "Описание", TaskStatus.NEW);

        fileBackedTaskManager.createTask(task);

        Task savedTask = fileBackedTaskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = fileBackedTaskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testFindTaskById() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);

        Task foundTask = fileBackedTaskManager.getTaskById(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }

    @Test
    void testIdConflict() {
        Task taskWithId = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskWithId.setId(1);

        Task taskWithoutId = new Task("Задача 2", "Описание 2", TaskStatus.NEW);

        fileBackedTaskManager.createTask(taskWithId);
        fileBackedTaskManager.createTask(taskWithoutId);

        assertNotNull(fileBackedTaskManager.getTaskById(1));
        assertNotNull(fileBackedTaskManager.getTaskById(2));

        assertFalse(fileBackedTaskManager.getTaskById(1).equals(fileBackedTaskManager.getTaskById(2)));
    }

}