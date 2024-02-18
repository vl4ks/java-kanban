import org.junit.jupiter.api.Test;

import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача", "Описание", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void testTasksWithDifferentIdAreNotEqual() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.DONE);

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2);
    }
}
