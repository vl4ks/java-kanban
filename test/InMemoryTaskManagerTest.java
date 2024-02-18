import org.junit.jupiter.api.Test;

import taskmanagement.task.*;
import taskmanagement.taskmanager.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    @Test
    void testCreateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory(), Managers.getDefaultHistory());
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void testCreateEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory(), Managers.getDefaultHistory());
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void testCreateSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory(), Managers.getDefaultHistory());
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void testUpdateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory(), Managers.getDefaultHistory());
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void testDeleteTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory(), Managers.getDefaultHistory());
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);

        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testAddDifferentTaskTypes() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(
                Managers.getDefaultHistory(),
                Managers.getDefaultHistory()
        );

        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, 1);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void testAddNewTask() {
        TaskManager taskManager = Managers.getDefault();
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
        InMemoryTaskManager taskManager = new InMemoryTaskManager(
                Managers.getDefaultHistory(),
                Managers.getDefaultHistory()
        );

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);

        Task foundTask = taskManager.getTaskById(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }

    @Test
    void testIdConflict() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager(), new InMemoryHistoryManager());

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

        // Проверяем, что id не конфликтуют, используя equals
        assertFalse(taskManager.getTaskById(1).equals(taskManager.getTaskById(2)));
    }

    @Test
    void testAdd() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);

        historyManager.add(task);

        // Получаем историю
        assertEquals(1, historyManager.getHistory().size());

        // Получаем предыдущую версию задачи
        Task previousVersion = historyManager.getHistory().get(0);

        // Проверяем, что предыдущая версия задачи равна оригинальной
        assertEquals(task, previousVersion);

        // Проверяем, что данные предыдущей версии задачи также равны оригинальным данным
        assertEquals(task.getTitle(), previousVersion.getTitle());
        assertEquals(task.getDescription(), previousVersion.getDescription());
        assertEquals(task.getStatus(), previousVersion.getStatus());
    }
}
