package taskmanagement.taskmanager;
import taskmanagement.task.TaskType;
import taskmanagement.task.TaskStatus;
import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter;  // счетчик для генерации id
    public TaskManager() {
        this.idCounter = 1;
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //удаление всех эпиков
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            List<Subtask> relatedSubtasks = getSubtasksByEpicId(epic.getId());
            for (Subtask subtask : relatedSubtasks) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.clear();
    }

    //получение списка всех подзадач
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // удаление всех подзадач
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    //создание задачи (или эпика, или подзадачи)
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
    }

    // получение списка всех задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // удаление всех задач
    public void removeAllTasks() {
        tasks.clear();
    }

    //получение задачи, подзадачи и эпика по id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public Task getSubtaskById(int id) {
        return subtasks.get(id);
    }
    public Task getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            if (TaskType.SUBTASK.equals(task.getType())) {
                subtasks.put(task.getId(), (Subtask) task);
            } else if (TaskType.EPIC.equals(task.getType())) {
                epics.put(task.getId(), (Epic) task);
                updateEpicStatus((Epic) task);
            }
        }
    }


    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.remove(id);
            if (TaskType.SUBTASK.equals(task.getType())) {
                if (subtasks.containsKey(id)) {
                    subtasks.remove(id);
                    int epicId = ((Subtask) task).getEpicId();
                    if (epics.containsKey(epicId)) {
                        updateEpicStatus(epics.get(epicId));
                    }
                }
            } else if (TaskType.EPIC.equals(task.getType())) {
                epics.remove(id);
                List<Subtask> relatedSubtasks = getSubtasksByEpicId(id);
                for (Subtask subtask : relatedSubtasks) {
                    if (subtasks.containsKey(subtask.getId())) {
                        subtasks.remove(subtask.getId());
                    }
                }
            }
        }
    }



    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Subtask subtaskId : epic.getSubtasks()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }

    public void updateEpicStatus(Epic epic) {
        if (epic != null) {
            List<Subtask> subtasksIds = epic.getSubtasks();
            boolean allSubtasksNew = true;
            boolean allSubtasksDone = true;
            for (Subtask subtaskId : subtasksIds) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allSubtasksDone = false;
                }
                if (subtask.getStatus() != TaskStatus.NEW) {
                    allSubtasksNew = false;
                }
            }
            if (allSubtasksDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (allSubtasksNew) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
