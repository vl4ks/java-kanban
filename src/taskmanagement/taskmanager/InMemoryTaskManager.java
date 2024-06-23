package taskmanagement.taskmanager;

import taskmanagement.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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

    private boolean isTimeOverlap(Task existTask, Task newTask) {
        LocalDateTime startOfExistTask = existTask.getStartTime();
        LocalDateTime endOfExistTask = existTask.getEndTime();
        LocalDateTime startOfNewTask = newTask.getStartTime();
        LocalDateTime endOfNewTask = newTask.getEndTime();

        if (startOfExistTask == null || endOfExistTask == null || startOfNewTask == null || endOfNewTask == null) {
            return false;
        }
        return (startOfExistTask.isBefore(endOfNewTask) && startOfNewTask.isBefore(endOfExistTask));
    }

    protected boolean isIntersection(Task newTask) {
        for (Task existTask : prioritizedTasks) {
            if (existTask.getId() != newTask.getId() && isTimeOverlap(existTask, newTask)) {
                return true;
            }
        }
        return false;
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
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
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
            prioritizedTasks.remove(subtask);
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
        if (task.getId() == null) {
            throw new NotFoundException("Задача не найдена");
        }
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getSubtasks() == null) {
            epic.setSubtasks(new ArrayList<>()); //// Инициализация пустого списка для успешного выполения запроса POST
        }
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
        } else {
            throw new NotFoundException("Эпик с id=" + subtask.getEpicId() + " не найден.");
        }
        prioritizedTasks.add(subtask);
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
            prioritizedTasks.remove(task);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) return;

        if (task.getType() == TaskType.TASK && isIntersection(task)) {
            throw new ValidationException("Пересечение задач");
        }

        Task oldTask = null;
        if (task.getType() == TaskType.TASK) {
            oldTask = tasks.get(task.getId());
            tasks.put(task.getId(), task);
        } else if (task.getType() == TaskType.SUBTASK) {
            oldTask = subtasks.get(task.getId());
            subtasks.put(task.getId(), (Subtask) task);
            Epic epic = epics.get(((Subtask) task).getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
                calculateEpicFields(epic);
            }
        } else if (task.getType() == TaskType.EPIC) {
            oldTask = epics.get(task.getId());
            epics.put(task.getId(), (Epic) task);
            calculateEpicFields((Epic) task);
            updateEpicStatus((Epic) task);
        }

        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
        }
        prioritizedTasks.add(task);
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            int epicId = subtask.getEpicId();
            prioritizedTasks.remove(subtask);

            if (epics.containsKey(epicId)) {
                Epic epic = epics.get(epicId);
                epic.getSubtasks().remove(Integer.valueOf(id));
                updateEpicStatus(epic);
                calculateEpicFields(epic);
            }

            historyManager.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            historyManager.remove(id);
            for (Integer subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtaskId);
            }
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
                .filter(Objects::nonNull)
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
