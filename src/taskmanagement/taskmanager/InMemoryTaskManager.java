package taskmanagement.taskmanager;

import taskmanagement.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager; // История просмотров через HistoryManager
    private HistoryManager lastViewedHistoryManager; // Последние просмотренные задачи
    private int idCounter;  // счетчик для генерации id


    public InMemoryTaskManager(HistoryManager historyManager, HistoryManager lastViewedHistoryManager) {
        this.idCounter = 1;
        this.historyManager = historyManager;
        this.lastViewedHistoryManager = lastViewedHistoryManager;
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
            for (Integer subtask : relatedSubtasks) {
                subtasks.remove(subtask);
            }
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
        tasks.clear();
    }

    //получение задачи, подзадачи и эпика по id
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task); // Добавление в историю просмотров
            updateLastViewedTasks(task); // Добавление в последние просмотренные задачи
        }
        return task;
    }

    @Override
    public Task getSubtaskById(int id) {
        Task subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            updateLastViewedTasks(subtask);
        }
        return subtask;
    }

    @Override
    public Task getEpicById(int id) {
        Task epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            updateLastViewedTasks(epic);
        }
        return epic;
    }

    private void updateLastViewedTasks(Task task) {
        if (task != null) {
            if (lastViewedHistoryManager.getLastViewed().size() >= 10) {
                lastViewedHistoryManager.getLastViewed().remove(0);
            }
            lastViewedHistoryManager.addLastViewed(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<TaskSnapshot> historySnapshots = historyManager.getHistory();
        List<Task> historyTasks = new ArrayList<>();

        for (TaskSnapshot snapshot : historySnapshots) {
            historyTasks.add(snapshot.restoreTask());
        }

        return historyTasks;
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

    @Override
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
