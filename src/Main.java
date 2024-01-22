import taskmanagement.task.Epic;
import taskmanagement.task.Subtask;
import taskmanagement.task.Task;
import taskmanagement.task.TaskStatus;
import taskmanagement.taskmanager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
// Создаем задачи, эпики и подзадачи
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, "Задача");
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS, "Задача");

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW, "Эпик");
        epic1.addSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId(), "Подзадача"));
        epic1.addSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.IN_PROGRESS, epic1.getId(), "Подзадача"));

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", TaskStatus.DONE, "Эпик");
        epic2.addSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.DONE, epic2.getId(), "Подзадача"));

        // Добавляем задачи в менеджер
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(epic1);
        taskManager.createTask(epic2);

        // Выводим списки задач
        System.out.println("Список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getId() + ": " + task.getTitle() + " - " + task.getStatus());
        }

        // Изменяем статусы задач
        task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.IN_PROGRESS, "Задача");
        epic1.addSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.DONE, epic1.getId(), "Подзадача"));

        taskManager.updateTask(task1);
        taskManager.updateTask(epic1);

        // Выводим обновленные списки задач
        System.out.println("\nОбновленный список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getId() + ": " + task.getTitle() + " - " + task.getStatus());
        }

        // Удаляем задачу и эпик
        taskManager.deleteTaskById(2);
        for (Epic epic : taskManager.getAllEpics()) {
            taskManager.updateEpicStatus(epic);
        }
        taskManager.deleteTaskById(3);

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
        for (Task task : taskManager.getAllTasks()) {
            if (task.getClass().equals(Subtask.class)) {
                System.out.println(task);
            }
        }
    }
}
