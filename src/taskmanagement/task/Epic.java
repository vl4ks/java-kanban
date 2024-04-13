package taskmanagement.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtasks = new ArrayList<>(); // список подзадач в эпике

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        this.type = TaskType.EPIC;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                ", type=" + getType() +
                '}';
    }
}
