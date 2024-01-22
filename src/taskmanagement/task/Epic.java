package taskmanagement.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks; // список подзадач в эпике
    private String type;
    public Epic(String title, String description, TaskStatus status, String type) {
        super(title, description, status, type);
        this.subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks) &&
                Objects.equals(type, epic.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks, type);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                ", type=" + type +
                '}';
    }
}

