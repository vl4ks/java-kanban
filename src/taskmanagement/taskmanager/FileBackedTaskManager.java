package taskmanagement.taskmanager;

import taskmanagement.task.*;

import java.io.*;

import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File saveFile;

    public FileBackedTaskManager(HistoryManager historyManager, File saveFile) {
        super(historyManager);
        this.saveFile = saveFile;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            List<Task> allTasks = getAllTasks();
            for (Task task : allTasks) {
                String csvLine = taskToCsv(task);
                writer.write(csvLine);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private String taskToCsv(Task task) {
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
            sb.append("");
        }
        return sb.toString();
    }

    public static FileBackedTaskManager loadFromFile(File saveFile) {
        FileBackedTaskManager taskManager = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            HistoryManager historyManager = new InMemoryHistoryManager();
            taskManager = new FileBackedTaskManager(historyManager, saveFile);
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = csvToTask(line);
                taskManager.createTask(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskManager;
    }

    private static Task csvToTask(String csvLine) {
        String[] parts = csvLine.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(parts[5]);
            return new Subtask(id, title, description, status, epicId);
        } else {
            return new Task(id, title, description, status);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    protected void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

}
