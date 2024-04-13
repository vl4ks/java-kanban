import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testLinkLast() {
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача", "Описание", TaskStatus.IN_PROGRESS);

        historyManager.linkLast(task1);
        historyManager.linkLast(task2);

        assertEquals(task1, historyManager.getTasks().get(0));
        assertEquals(task2, historyManager.getTasks().get(1));

    }

    @Test
    void testRemoveNode() {
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача", "Описание", TaskStatus.IN_PROGRESS);

        task1.setId(1);
        task2.setId(2);

        historyManager.linkLast(task1);
        historyManager.linkLast(task2);

        historyManager.removeNode(historyManager.taskNodes.get(task1.getId()));
        assertEquals(task2, historyManager.getTasks().get(0));
        assertFalse(historyManager.getTasks().contains(task1));

    }

    @Test
    void testAdd() {
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача", "Описание", TaskStatus.IN_PROGRESS);

        task1.setId(1);
        task2.setId(1);
        historyManager.linkLast(task1);
        historyManager.add(task2);
        assertEquals(task2, historyManager.getTasks().get(0));
    }

    @Test
    void testRemove() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);

        task.setId(1);
        historyManager.linkLast(task);
        historyManager.remove(1);
        assertEquals(0, historyManager.getTasks().size());
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        task1.setId(1);
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);

        assertNotNull(historyManager.getHistory());
        assertEquals(2, historyManager.getHistory().size());

        assertNotSame(task1, task2);

        assertEquals(task1.getTitle(), historyManager.getHistory().get(0).getTitle());
        assertEquals(task1.getDescription(), historyManager.getHistory().get(0).getDescription());
        assertEquals(task1.getStatus(), historyManager.getHistory().get(0).getStatus());

        assertEquals(task2.getTitle(), historyManager.getHistory().get(1).getTitle());
        assertEquals(task2.getDescription(), historyManager.getHistory().get(1).getDescription());
        assertEquals(task2.getStatus(), historyManager.getHistory().get(1).getStatus());
    }
}
