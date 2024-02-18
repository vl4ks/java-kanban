package taskmanagement.taskmanager;

import taskmanagement.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;  // Максимальный размер истории

    private List<Task> history = new ArrayList<>();
    private List<Task> lastViewed = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            Task taskCopy = task.shallowCopy();
            history.add(taskCopy);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void addLastViewed(Task task) {
        if (task != null) {
            Task taskCopy = task.shallowCopy();
            if (lastViewed.size() >= MAX_HISTORY_SIZE) {
                lastViewed.remove(0);
            }
            lastViewed.add(taskCopy);
        }
    }

    public List<Task> getLastViewed() {
        return new ArrayList<>(lastViewed);
    }
}
