package taskmanagement.taskmanager;

import taskmanagement.task.Task;
import taskmanagement.task.TaskSnapshot;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<TaskSnapshot> getHistory();

    void addLastViewed(Task task);

    List<Task> getLastViewed();
}