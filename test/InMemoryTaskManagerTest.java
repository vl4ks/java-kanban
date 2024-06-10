import org.junit.jupiter.api.BeforeEach;
import taskmanagement.taskmanager.InMemoryTaskManager;
import taskmanagement.taskmanager.Managers;
import taskmanagement.taskmanager.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
