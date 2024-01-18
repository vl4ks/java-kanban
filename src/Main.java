public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создаем задачи, эпики и подзадачи
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);

        Epic epic1 = new Epic(3, "Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        epic1.addSubtask(new Subtask(4, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId()));
        epic1.addSubtask(new Subtask(5, "Подзадача 2", "Описание подзадачи 2", TaskStatus.IN_PROGRESS, epic1.getId()));

        Epic epic2 = new Epic(6, "Эпик 2", "Описание эпика 2", TaskStatus.DONE);
        epic2.addSubtask(new Subtask(7, "Подзадача 3", "Описание подзадачи 3", TaskStatus.DONE, epic2.getId()));

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
        task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.IN_PROGRESS);
        epic1.addSubtask(new Subtask(4, "Подзадача 1", "Описание подзадачи 1", TaskStatus.DONE, epic1.getId()));

        taskManager.updateTask(task1);
        taskManager.updateTask(epic1);

        // Выводим обновленные списки задач
        System.out.println("\nОбновленный список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getId() + ": " + task.getTitle() + " - " + task.getStatus());
        }

        // Удаляем задачу и эпик
        taskManager.deleteTaskById(2);
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
            if (task instanceof Subtask) {
                System.out.println(task);
            }
        }
    }
}
