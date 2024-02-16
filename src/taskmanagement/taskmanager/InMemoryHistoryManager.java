package taskmanagement.taskmanager;

import taskmanagement.task.Task;
import taskmanagement.task.TaskSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<TaskSnapshot> history = new ArrayList<>();

    private List<Task> lastViewed = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            TaskSnapshot taskSnapshot = createSnapshot(task.shallowCopy());
            history.add(taskSnapshot);
        }
    }

    private TaskSnapshot createSnapshot(Task task) {
        return new TaskSnapshot(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
    }

    @Override
    public List<TaskSnapshot> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void addLastViewed(Task task) {
        if (task != null) {
            Task taskCopy = task.shallowCopy();
            if (lastViewed.size() >= 10) {
                lastViewed.remove(0);
            }
            lastViewed.add(taskCopy);
        }
    }

    public List<Task> getLastViewed() {
        return new ArrayList<>(lastViewed);
    }
}
