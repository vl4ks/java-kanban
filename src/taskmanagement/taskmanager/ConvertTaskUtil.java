package taskmanagement.taskmanager;

import taskmanagement.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ConvertTaskUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String taskToCsv(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getEpicId()).append(",");
        } else {
            sb.append("null,");
        }

        if (task.getDuration() != null) {
            sb.append(task.getDuration().toMinutes()).append(",");
        } else {
            sb.append("null,");
        }

        if (task.getStartTime() != null) {
            sb.append(task.getStartTime().format(DATE_TIME_FORMATTER));
        } else {
            sb.append("null");
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
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = null;
        Integer epicId = null;

        if (parts.length > 5 && !parts[5].isBlank() && !parts[5].equals("null")) {
            epicId = Integer.parseInt(parts[5]);
        }
        if (parts.length > 6 && !parts[6].isBlank() && !parts[6].equals("null")) {
            duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        }
        if (parts.length > 7 && !parts[7].isBlank() && !parts[7].equals("null")) {
            startTime = LocalDateTime.parse(parts[7], DATE_TIME_FORMATTER);
        }

        switch (type) {
            case EPIC:
                return new Epic(id, title, description, status, duration, startTime);
            case SUBTASK:
                return new Subtask(id, title, description, status, epicId, duration, startTime);
            default:
                return new Task(id, title, description, status, duration, startTime);
        }
    }
}
