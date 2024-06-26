import org.junit.jupiter.api.Test;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.InMemoryHistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    @Test
    void testAdd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task(2, "Задача2", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusHours(1));

        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
    }

    @Test
    void testAddDuplicate() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task(1, "Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
    }

    @Test
    void testRemoveFirst() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task(2, "Задача2", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task3 = new Task(3, "Задача3", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }

    @Test
    void testRemoveMiddle() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task(2, "Задача2", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task3 = new Task(3, "Задача3", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }

    @Test
    void testRemoveLast() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача1", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task(2, "Задача2", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task3 = new Task(3, "Задача3", "Описание", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
    }
}
