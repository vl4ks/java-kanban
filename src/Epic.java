import java.util.ArrayList;
import java.util.List;
class Epic extends Task {

    private List<Subtask> subtasks; // список подзадач в эпике

    public Epic(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        this.subtasks = new ArrayList<>();
    }
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
