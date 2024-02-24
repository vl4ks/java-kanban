import taskmanagement.task.*;
import taskmanagement.taskmanager.InMemoryTaskManager;
import taskmanagement.taskmanager.Managers;


public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager(
                Managers.getDefaultHistory());


        // Создаем задачи, эпики и подзадачи
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", TaskStatus.DONE);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.DONE, epic2.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Добавляем задачи в историю
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subtask1.getId());
        taskManager.getTaskById(epic1.getId());

        System.out.println("Список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getId() + ": " + task.getTitle() + " - " + task.getStatus());
        }

        task1.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);
        epic1.setStatus(TaskStatus.IN_PROGRESS);
        epic2.setStatus(TaskStatus.DONE);


        // Обновляем задачи, подзадачи и эпики
        taskManager.updateTask(task1);
        taskManager.updateTask(subtask1);
        taskManager.updateTask(epic1);
        taskManager.updateTask(epic2);

        // Выводим обновленные списки задач
        System.out.println("\nОбновленный список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getId() + ": " + task.getTitle() + " - " + task.getStatus());
        }
        // Удаляем задачу и эпик
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteTaskById(epic1.getId());

        // Выводим окончательные списки задач
        System.out.println("\nОкончательный список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getId() + ": " + task.getTitle() + " - " + task.getStatus());
        }
        //Вывод строкового представления задач, эпиков, подзадач

        System.out.println("Список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nСписок эпиков:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nСписок подзадач:");
        for (Task task : taskManager.getAllSubtasks()) {
            if (task.getClass().equals(Subtask.class)) {
                System.out.println(task);
            }
        }
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

    }
}
