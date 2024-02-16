import org.junit.jupiter.api.Test;
import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.InMemoryTaskManager;
import taskmanagement.taskmanager.Managers;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

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
        taskManager.createTask(epic);
        taskManager.createTask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getTaskById(epic.getId()));
        assertEquals(subtask, taskManager.getTaskById(subtask.getId()));
    }
}
