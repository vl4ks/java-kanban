import org.junit.jupiter.api.Test;

import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicEqualsById() {
        Epic epic1 = new Epic("Эпик", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик", "Описание", TaskStatus.NEW);

        epic1.setId(3);
        epic2.setId(3);

        assertEquals(epic1, epic2);
    }

    @Test
    void testAddSubtaskToEpicSelf() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.IN_PROGRESS);
        epic.setId(1);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, 1);
        subtask.setId(2);
        epic.addSubtask(subtask.getId());

        assertNotEquals(epic, subtask);
    }

    private void addSubtaskToEpic(Epic epic, int subtaskId) {
        epic.addSubtask(subtaskId);
    }

    @Test
    void testEpicAddSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика", TaskStatus.NEW);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, 1);
        epic.addSubtask(subtask.getId());
        assertTrue(epic.getSubtasks().contains(subtask.getId()));
    }
}