import org.junit.jupiter.api.Test;

import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.InMemoryTaskManager;
import taskmanagement.taskmanager.Managers;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    @Test
    void testSubtaskEqualsById() {
        Subtask subtask1 = new Subtask("Подзадача", "Описание", TaskStatus.NEW, 1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("Подзадача", "Описание", TaskStatus.NEW, 1);
        subtask2.setId(2);

        assertEquals(subtask1, subtask2);
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, 1);
        subtask.setId(1);

        Epic epic = new Epic("Эпик 1", "Описание", TaskStatus.IN_PROGRESS);
        epic.setId(1);
        epic.addSubtask(subtask.getId());

        assertNotEquals(epic, subtask);
    }

    private void createSubtaskInManager(Subtask subtask) {
        new InMemoryTaskManager(Managers.getDefaultHistory()).createSubtask(subtask);
    }

}
