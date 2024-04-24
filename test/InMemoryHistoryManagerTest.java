import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

            Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
            Task task2 = new Task("Задача", "Описание", TaskStatus.NEW);
            Task task3 = new Task("Задача", "Описание", TaskStatus.NEW);

            taskManager.createTask(task1);
            taskManager.createTask(task2);
            taskManager.createTask(task3);
            taskManager.getTaskById(1);
            taskManager.getTaskById(2);
            taskManager.getTaskById(3);
    }

    @Test
    void testRemoveFirst() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        for (Task h : history) {
            System.out.println(h);

        }
        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
    }
    @Test
    void testRemoveCentral() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();

        for (Task h : history) {
            System.out.println(h);

        }
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
    }
    @Test
    void testRemoveLast() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();
        for (Task h : history) {
            System.out.println(h);

        }
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
    }

}
