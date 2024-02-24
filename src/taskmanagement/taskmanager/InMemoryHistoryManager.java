package taskmanagement.taskmanager;

import taskmanagement.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;  // Максимальный размер истории

    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            Task taskCopy = task.shallowCopy();
            if (history.size() >= MAX_HISTORY_SIZE) {
                history.remove(0);
            }
            history.add(taskCopy);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
