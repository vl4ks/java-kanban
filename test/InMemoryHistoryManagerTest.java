import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testCreateTasksAndRemove() {
        InMemoryHistoryManager historyManager = mock(InMemoryHistoryManager.class);
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW);
        Task task3 = new Task("Задача3", "Описание", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);

        when(historyManager.getHistory()).thenReturn(Arrays.asList(task1, task2, task3));

        taskManager.deleteTaskById(1);

        verify(historyManager).remove(task1.getId());

        taskManager.deleteTaskById(2);

        verify(historyManager).remove(task2.getId());

        taskManager.deleteTaskById(3);

        verify(historyManager).remove(task3.getId());

    }

    @Test
    void testAdd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        for (Task h : history) {
            System.out.println(h);

        }
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void testRemoveLast() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW);
        Task task3 = new Task("Задача3", "Описание", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        for (Task h : history) {
            System.out.println(h);

        }
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }
}
