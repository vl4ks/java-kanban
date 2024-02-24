import org.junit.jupiter.api.Test;

import taskmanagement.task.Task;
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

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());

        // Проверяем, что объект в списке не является тем же самым объектом, что и добавленный
        assertNotSame(task, history.get(0));

        // Проверяем, что поля объекта равны
        assertEquals(task.getTitle(), history.get(0).getTitle());
        assertEquals(task.getDescription(), history.get(0).getDescription());
        assertEquals(task.getStatus(), history.get(0).getStatus());
    }

    @Test
    void testGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(2, history.size());


        assertNotSame(task1, history.get(0));
        assertNotSame(task2, history.get(1));


        assertEquals(task1.getTitle(), history.get(0).getTitle());
        assertEquals(task1.getDescription(), history.get(0).getDescription());
        assertEquals(task1.getStatus(), history.get(0).getStatus());

        assertEquals(task2.getTitle(), history.get(1).getTitle());
        assertEquals(task2.getDescription(), history.get(1).getDescription());
        assertEquals(task2.getStatus(), history.get(1).getStatus());
    }
}
