package taskmanagement.taskmanager;

import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskType;

import java.io.*;
import java.util.Comparator;
import java.util.TreeSet;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File saveFile;

    public FileBackedTaskManager(HistoryManager historyManager, File saveFile) {
        super(historyManager);
        this.saveFile = saveFile;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write("id,type,name,status,description,epic,duration,startTime");
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(ConvertTaskUtil.taskToCsv(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(ConvertTaskUtil.taskToCsv(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(ConvertTaskUtil.taskToCsv(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerIOException("Произошла ошибка во время записи файла.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File saveFile) {
        FileBackedTaskManager taskManager = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            HistoryManager historyManager = new InMemoryHistoryManager();
            taskManager = new FileBackedTaskManager(historyManager, saveFile);
            String line;

            taskManager.prioritizedTasks.clear();
            while ((line = reader.readLine()) != null) {
                if (line.equals("id,type,name,status,description,epic,duration,startTime")) {
                    continue;
                }
                Task task = ConvertTaskUtil.csvToTask(line);
                if (task.getType() == TaskType.EPIC) {
                    taskManager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    Subtask subtask = (Subtask) task;
                    taskManager.subtasks.put(task.getId(), subtask);
                    Epic epic = taskManager.epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtask(subtask.getId());
                    }
                    taskManager.prioritizedTasks.add(subtask);
                } else {
                    taskManager.tasks.put(task.getId(), task);
                    taskManager.prioritizedTasks.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskManager;
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
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }
}
