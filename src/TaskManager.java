import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    private int idCounter;  // счетчик для генерации id

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.idCounter = 1;
    }
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //удаление всех эпиков
    public void removeAllEpics() {
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

        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
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

    //получение задачи по id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // обновление задачи
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);

        }
    }

    // удаление задачи по id
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    //получение списка всех подзадач определённого эпика
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask && ((Subtask) task).getEpicId() == epicId) {
                result.add((Subtask) task);
            }
        }
        return result;
    }
    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId());
        boolean allSubtasksDone = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allSubtasksDone = false;
                break;
            }
        }

        if (allSubtasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
