package taskmanagement.taskmanager;

import taskmanagement.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}