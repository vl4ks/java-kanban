package taskmanagement.task;

import java.util.Objects;

public class Task {
    private int id;
    protected String title;
    protected String description;
    protected TaskStatus status;

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String title, String description, TaskStatus status) {
        this(title, description, status);
        this.id = id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
