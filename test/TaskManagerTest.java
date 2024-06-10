import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.NotFoundException;
import taskmanagement.taskmanager.TaskManager;
import taskmanagement.taskmanager.ValidationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void testCreateTask() {
        System.out.println(taskManager.getAllTasks());
        Task task = new Task("Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 10, 30));
        taskManager.createTask(task);
        System.out.println(taskManager.getAllTasks());
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(task, taskManager.getTaskById(task.getId()), "Задачи не совпадают.");
    }

    @Test
    void testCreateEpic() {
        System.out.println(taskManager.getAllEpics());
        Epic epic = new Epic("Эпик", "Описание", TaskStatus.NEW);
        taskManager.createEpic(epic);
        System.out.println(taskManager.getAllEpics());
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпики не совпадают.");
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Эпик1", "Описание", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача5", "Описание", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 11, 00));

        taskManager.createSubtask(subtask);
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество подзадач.");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают.");
    }

    @Test
    void testEpicFieldCalculation() {
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());
        Epic epic = new Epic("Эпик", "Описание эпика", TaskStatus.NEW);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Subtask subtask1 = new Subtask("Подзадача1", "Описание", TaskStatus.NEW, 1, Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 11, 30));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание", TaskStatus.NEW, 1, Duration.ofMinutes(30), LocalDateTime.of(2024, 4, 5, 11, 50));
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());

        assertEquals(LocalDateTime.of(2024, 4, 5, 11, 30).format(formatter), epic.getStartTime().format(formatter));
        assertEquals(LocalDateTime.of(2024, 4, 5, 12, 20).format(formatter), epic.getEndTime().format(formatter));
        assertEquals(Duration.ofMinutes(45), epic.getDuration());
    }

    @Test
    void testTaskIntersection() {
        Task task1 = new Task("Задача 1.1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 4, 5, 12, 30));
        Task task2 = new Task("Задача 2.2", "Описание", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 4, 5, 12, 50));
        taskManager.createTask(task1);
        System.out.println(taskManager.getAllTasks());
        ValidationException exception = assertThrows(ValidationException.class, () -> taskManager.createTask(task2));
        assertEquals("Пересечение задач", exception.getMessage());
    }

    @Test
    void testEpicUpdateStatus() {
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());

        Epic epic = new Epic("Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 3", "Описание", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 13, 00));
        Subtask subtask2 = new Subtask("Подзадача 4", "Описание", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.of(2024, 4, 5, 14, 00));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus());

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }


    @Test
    void testUpdateTask() {
        System.out.println(taskManager.getAllTasks());
        Task task = new Task("Задача112", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 14, 40));
        taskManager.createTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        System.out.println(taskManager.getAllTasks());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void testDeleteTask() {
        System.out.println(taskManager.getAllTasks());
        Task task = new Task("Задача11", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 15, 00));
        taskManager.createTask(task);

        System.out.println(taskManager.getAllTasks());
        assertNotNull(task, "Задача после создания не должна быть null");
        assertNotNull(task.getId(), "У задачи должен быть установлен id");

        taskManager.deleteTaskById(task.getId());
        System.out.println(taskManager.getAllTasks());

        assertTrue(taskManager.getAllTasks().isEmpty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskManager.getTaskById(task.getId()));
        assertEquals("Задача с id=" + task.getId() + " не найдена.", exception.getMessage());

    }

    @Test
    void testRemoveAllTasks() {
        System.out.println(taskManager.getAllTasks());
        Task task1 = new Task("Задача 1.1.1", "Описание 1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 15, 20));
        Task task2 = new Task("Задача 2.2.2", "Описание 2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 15, 40));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println(taskManager.getAllTasks());
        taskManager.removeAllTasks();
        System.out.println(taskManager.getAllTasks());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void testRemoveAllSubtasks() {
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());

        Epic epic = new Epic("Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача6", "Описание", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 16, 00));
        Subtask subtask2 = new Subtask("Подзадача7", "Описание", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 16, 15));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());

        taskManager.removeAllSubtasks();
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(epic.getSubtasks().isEmpty());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void testRemoveAllEpics() {
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Сабтаски:");
        System.out.println(taskManager.getAllSubtasks());

        Epic epic1 = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика", TaskStatus.IN_PROGRESS);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача8", "Описание", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 16, 40));
        Subtask subtask2 = new Subtask("Подзадача9", "Описание", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 4, 5, 16, 55));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);


        taskManager.removeAllEpics();


        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }
}
