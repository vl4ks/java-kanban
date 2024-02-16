import org.junit.jupiter.api.Test;
import taskmanagement.task.Task;
import taskmanagement.task.TaskSnapshot;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.InMemoryHistoryManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testAdd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);

        historyManager.add(task);

        List<TaskSnapshot> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());

        // Проверяем, что объект в списке не является тем же самым объектом, что и добавленный
        assertNotSame(task, history.get(0));

        // Проверяем, что поля объекта равны
        assertEquals(task.getTitle(), history.get(0).getTaskTitle());
        assertEquals(task.getDescription(), history.get(0).getTaskDescription());
        assertEquals(task.getStatus(), history.get(0).getTaskStatus());
    }

    @Test
    void testGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        List<TaskSnapshot> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(2, history.size());

        // Проверяем, что объекты в списке не являются теми же самыми объектами, что и добавленные
        assertNotSame(task1, history.get(0));
        assertNotSame(task2, history.get(1));

        // Проверяем, что поля объектов равны
        assertEquals(task1.getTitle(), history.get(0).getTaskTitle());
        assertEquals(task1.getDescription(), history.get(0).getTaskDescription());
        assertEquals(task1.getStatus(), history.get(0).getTaskStatus());

        assertEquals(task2.getTitle(), history.get(1).getTaskTitle());
        assertEquals(task2.getDescription(), history.get(1).getTaskDescription());
        assertEquals(task2.getStatus(), history.get(1).getTaskStatus());
    }

    @Test
    void testAddLastViewed() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        historyManager.addLastViewed(task1);
        historyManager.addLastViewed(task2);

        List<Task> lastViewed = historyManager.getLastViewed();

        assertNotNull(lastViewed);
        assertEquals(2, lastViewed.size());

        // Проверяем, что объекты в списке не являются теми же самыми объектами, что и добавленные
        assertNotSame(task1, lastViewed.get(0));
        assertNotSame(task2, lastViewed.get(1));

        // Проверяем, что поля объектов равны
        assertEquals(task1.getTitle(), lastViewed.get(0).getTitle());
        assertEquals(task1.getDescription(), lastViewed.get(0).getDescription());
        assertEquals(task1.getStatus(), lastViewed.get(0).getStatus());

        assertEquals(task2.getTitle(), lastViewed.get(1).getTitle());
        assertEquals(task2.getDescription(), lastViewed.get(1).getDescription());
        assertEquals(task2.getStatus(), lastViewed.get(1).getStatus());
    }

    @Test
    void testGetLastViewed() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        historyManager.addLastViewed(task1);
        historyManager.addLastViewed(task2);

        List<Task> lastViewed = historyManager.getLastViewed();

        assertNotNull(lastViewed);
        assertEquals(2, lastViewed.size());

        // Проверяем, что объекты в списке не являются теми же самыми объектами, что и добавленные
        assertNotSame(task1, lastViewed.get(0));
        assertNotSame(task2, lastViewed.get(1));

        // Проверяем, что поля объектов равны
        assertEquals(task1.getTitle(), lastViewed.get(0).getTitle());
        assertEquals(task1.getDescription(), lastViewed.get(0).getDescription());
        assertEquals(task1.getStatus(), lastViewed.get(0).getStatus());

        assertEquals(task2.getTitle(), lastViewed.get(1).getTitle());
        assertEquals(task2.getDescription(), lastViewed.get(1).getDescription());
        assertEquals(task2.getStatus(), lastViewed.get(1).getStatus());
    }
}
