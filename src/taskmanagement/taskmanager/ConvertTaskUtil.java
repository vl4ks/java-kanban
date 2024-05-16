package taskmanagement.taskmanager;

import taskmanagement.task.*;

public final class ConvertTaskUtil {

    public static String taskToCsv(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getEpicId());
        } else {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static Task csvToTask(String csvLine) {
        String[] parts = csvLine.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case EPIC:
                return new Epic(id, title, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, title, description, status, epicId);
            default:
                return new Task(id, title, description, status);
        }
    }
}
