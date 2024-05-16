package taskmanagement.taskmanager;

import taskmanagement.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager; // История просмотров через HistoryManager
    private int idCounter;  // счетчик для генерации id


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.idCounter = 1;
        this.historyManager = historyManager;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //удаление всех эпиков
    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> relatedSubtasks = epic.getSubtasks();
            for (Integer subtaskId : relatedSubtasks) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    //получение списка всех подзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // удаление всех подзадач
    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    //создание задачи (или эпика, или подзадачи)
    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
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
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // удаление всех задач
    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    //получение задачи, подзадачи и эпика по id
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task getSubtaskById(int id) {
        Task subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task getEpicById(int id) {
        Task epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && task.getType() == TaskType.TASK) {
            tasks.put(task.getId(), task);
        }
        if (task != null && task.getType() == TaskType.SUBTASK) {
            subtasks.put(task.getId(), (Subtask) task);
        } else if (task != null && task.getType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
            updateEpicStatus((Epic) task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subtaskId : epic.getSubtasks()) {
                if (subtasks.containsKey(subtaskId)) {
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
            }
            epics.remove(id);
            historyManager.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            subtasks.remove(id);
            if (epics.containsKey(epicId)) {
                updateEpicStatus(epics.get(epicId));
            }
            historyManager.remove(id);
            getHistory().remove(id);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                result.add(subtask);
            }
        }
        return result;
    }

    protected void updateEpicStatus(Epic epic) {
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
