package taskmanagement.taskmanager;

import taskmanagement.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected static HashMap<Integer, Subtask> subtasks = new HashMap<>();
    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager historyManager;
    private int idCounter;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.idCounter = 1;
        this.historyManager = historyManager;
    }

    protected void calculateEpicFields(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }
        LocalDateTime minStartTime = LocalDateTime.MAX;
        LocalDateTime maxEndTime = LocalDateTime.MIN;
        Duration totalDuration = Duration.ZERO;

        for (Integer subtaskId : epic.getSubtasks()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStartTime().isBefore(minStartTime)) {
                    minStartTime = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(maxEndTime)) {
                    maxEndTime = subtask.getEndTime();
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        epic.setStartTime(minStartTime);
        epic.setDuration(totalDuration);
        epic.setEndTime(maxEndTime);
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return (start1.isBefore(end2) && start2.isBefore(end1));
    }

    protected boolean isIntersection(Task newTask) {
        return tasks.values().stream().anyMatch(existingTask -> isTimeOverlap(existingTask, newTask)) ||
                epics.values().stream().anyMatch(existingEpic -> isTimeOverlap(existingEpic, newTask)) ||
                subtasks.values().stream().anyMatch(existingSubtask -> isTimeOverlap(existingSubtask, newTask));
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
            calculateEpicFields(epic);
        }
    }

    //создание задачи (или эпика, или подзадачи)
    @Override
    public void createTask(Task task) {
        if (isIntersection(task)) {
            throw new ValidationException("Пересечение задач");
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        calculateEpicFields(epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isIntersection(subtask)) {
            throw new ValidationException("Пересечение задач");
        }
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
            calculateEpicFields(epic);
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
        } else {
            throw new NotFoundException("Задача с id=" + id + " не найдена.");
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

    public List<Task> getPrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(epics.values().stream()
                .filter(task -> task.getStartTime() != null).toList());
        prioritizedTasks.addAll(subtasks.values().stream()
                .filter(task -> task.getStartTime() != null).toList());
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && task.getType() == TaskType.TASK) {
            if (isIntersection(task)) {
                throw new ValidationException("Пересечение задач");
            }
            tasks.put(task.getId(), task);
        } else if (task != null && task.getType() == TaskType.SUBTASK) {
            subtasks.put(task.getId(), (Subtask) task);
            Epic epic = epics.get(((Subtask) task).getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
                calculateEpicFields(epic);
            }
        } else if (task != null && task.getType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
            calculateEpicFields((Epic) task);
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
                calculateEpicFields(epics.get(epicId));
            }
            historyManager.remove(id);
            getHistory().remove(id);
        } else {
            throw new NotFoundException("Задача с id=" + id + " не найдена.");
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик с id=" + epicId + " не найден.");
        }
        return epic.getSubtasks().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .collect(Collectors.toList());
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
