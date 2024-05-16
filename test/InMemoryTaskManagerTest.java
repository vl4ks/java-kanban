import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanagement.task.*;
import taskmanagement.taskmanager.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void testCreateTasksAndRemove() {
        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW);
        Task task3 = new Task("Задача3", "Описание", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertTrue(history.contains(task3));

        taskManager.deleteTaskById(task1.getId());
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task2));
        assertTrue(history.contains(task3));

        taskManager.deleteTaskById(task2.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task3));

        taskManager.deleteTaskById(task3.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void testCreateTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(task, taskManager.getTaskById(task.getId()), "Задачи не совпадают.");
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпики не совпадают.");
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество подзадач.");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают.");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);
        assertNotNull(task, "Задача после создания не должна быть null");
        assertNotNull(task.getId(), "У задачи должен быть установлен ID");
        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()), "Задача не удалена.");
    }


    @Test
    void testAddDifferentTaskTypes() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, 1);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество подзадач.");

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задачи не совпадают.");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпики не совпадают.");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают.");
    }

    @Test
    void testAddNewTask() {
        Task task = new Task("Задача добавлена", "Описание", TaskStatus.NEW);

        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testFindTaskById() {

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);

        Task foundTask = taskManager.getTaskById(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }

    @Test
    void testIdConflict() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        // Создаем задачу с явно заданным id
        Task taskWithId = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskWithId.setId(1);


        // Создаем задачу без явно заданного id (будет сгенерировано)
        Task taskWithoutId = new Task("Задача 2", "Описание 2", TaskStatus.NEW);


        // Добавляем задачи в менеджер
        taskManager.createTask(taskWithId);
        taskManager.createTask(taskWithoutId);

        // Проверяем, что задачи добавлены
        assertNotNull(taskManager.getTaskById(1));
        assertNotNull(taskManager.getTaskById(2));

        // Проверяем, что id не конфликтуют
        assertFalse(taskManager.getTaskById(1).equals(taskManager.getTaskById(2)));
    }

}
