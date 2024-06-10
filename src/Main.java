import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.*;

import java.time.Duration;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) {
        HistoryManager historyManager = new InMemoryHistoryManager();

        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", TaskStatus.DONE);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.IN_PROGRESS, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.DONE, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now());

        taskManager.createTask(task1);
        taskManager.createTask(task2);

//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//        taskManager.createSubtask(subtask3);

        System.out.println("Запрос 1:");
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));

        System.out.println("\nИстория после запроса 1:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\nЗапрос 2:");
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));

        System.out.println("\nИстория после запроса 2:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteTaskById(task1.getId());
        historyManager.remove(task1.getId());

        System.out.println("\nИстория после удаления задачи:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteTaskById(epic1.getId());
        historyManager.remove(epic1.getId());

        System.out.println("\nИстория после удаления эпика и его подзадач:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}