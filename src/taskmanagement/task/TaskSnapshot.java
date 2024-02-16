package taskmanagement.task;

public class TaskSnapshot {
    private final int taskId;
    private final String taskTitle;
    private final String taskDescription;
    private final TaskStatus taskStatus;

    public TaskSnapshot(int taskId, String taskTitle, String taskDescription, TaskStatus taskStatus) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public Task restoreTask() {
        Task task = new Task(taskTitle, taskDescription, taskStatus);
        task.setId(taskId);
        return task;
    }
}
