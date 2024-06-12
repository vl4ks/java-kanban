package taskmanagement.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks = new ArrayList<>(); // список подзадач в эпике
    protected LocalDateTime endTime;

    public Epic(int id, String title, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
    }

    public Epic(String title, String description, TaskStatus status) {
        this(0, title, description, status, Duration.ZERO, null);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
