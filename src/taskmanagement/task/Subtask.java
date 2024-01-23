package taskmanagement.task;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId; //Идентификатор эпика, к которому принадлежит подзадача

    public Subtask(String title, String description, TaskStatus status, int epicId, TaskType type) {
        super(title, description, status, type);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Subtask subtask = (Subtask) obj;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", type=" + getType() +
                '}';
    }
}
