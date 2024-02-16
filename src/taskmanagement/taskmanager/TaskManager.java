package taskmanagement.taskmanager;

import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;

import java.util.List;

public interface TaskManager {
    List<Epic> getAllEpics();

    //удаление всех эпиков
    void removeAllEpics();

    //получение списка всех подзадач
    List<Subtask> getAllSubtasks();

    // удаление всех подзадач
    void removeAllSubtasks();

    //создание задачи (или эпика, или подзадачи)
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    // получение списка всех задач
    List<Task> getAllTasks();

    // удаление всех задач
    void removeAllTasks();

    //получение задачи, подзадачи и эпика по id
    Task getTaskById(int id);

    Task getSubtaskById(int id);

    Task getEpicById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}
