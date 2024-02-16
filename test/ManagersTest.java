import org.junit.jupiter.api.Test;
import taskmanagement.task.Task;
import taskmanagement.task.TaskSnapshot;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testAddNewTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Задача добавлена", "Описание", TaskStatus.NEW);

        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void testFindTaskById() {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);

        Task foundTask = taskManager.getTaskById(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }

    @Test
    void testIdConflict() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager(), new InMemoryHistoryManager());

        // Создаем задачу с явно заданным id
        Task taskWithId = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskWithId.setId(1);


        // Создаем задачу без явно заданного id (будет сгенерировано)
        Task taskWithoutId = new Task("Задача 2", "Описание 2", TaskStatus.NEW);


        // Добавляем задачи в менеджер
        taskManager.createTask(taskWithId);
        taskManager.createTask(taskWithoutId);

        // Проверяем, что задачи добавлены
        assertNotNull(taskManager.getTaskById(1));
        assertNotNull(taskManager.getTaskById(2));

        // Проверяем, что id не конфликтуют, используя equals
        assertFalse(taskManager.getTaskById(1).equals(taskManager.getTaskById(2)));
    }


    @Test
    void testAdd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);

        historyManager.add(task);

        // Получаем историю
        assertEquals(1, historyManager.getHistory().size());

        // Получаем снимок (snapshot) истории
        TaskSnapshot snapshot = historyManager.getHistory().get(0);

        // Восстанавливаем задачу из снимка
        Task restoredTask = snapshot.restoreTask();

        // Проверяем, что восстановленная задача равна оригинальной
        assertEquals(task, restoredTask);
    }

}
