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
            List<Integer> relatedSubtasks = epic.getSubtasks();
            for (Integer subtask : relatedSubtasks) {
                subtasks.remove(subtask);
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
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(TaskStatus.NEW);
        }
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
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
        }
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
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            int epicId = subtasks.get(id).getEpicId();
            if (epics.containsKey(epicId)) {
                updateEpicStatus(epics.get(epicId));
            }
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                int subTaskId = subtask.getId();
                result.add(subtask);
            }
        }
        return result;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic != null) {
            List<Integer> subtasksIds = epic.getSubtasks();
            boolean allSubtasksNew = true;
            boolean allSubtasksDone = true;
            for (Integer subtaskId : subtasksIds) {
                TaskStatus subtaskStatus = subtasks.get(subtaskId).getStatus();
                if (subtaskStatus != TaskStatus.DONE) {
                    allSubtasksDone = false;
                }
                if (subtaskStatus != TaskStatus.NEW) {
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
