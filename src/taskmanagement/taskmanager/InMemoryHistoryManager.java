package taskmanagement.taskmanager;

import taskmanagement.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head = null;
    private Node tail = null;
    public HashMap<Integer, Node> taskNodes = new HashMap<>();
    public static LinkedHashMap<Integer, Task> linkTasks = new LinkedHashMap<>();

    public class Node {
        Task task;
        Node next;
        Node previous;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.previous = null;
        }
    }

    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
        }
        taskNodes.put(task.getId(), newNode);
        linkTasks.put(task.getId(), task);

    }

    public void removeNode(Node node) {
        if (node.previous != null) {
            node.previous.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            tail = node.previous;
        }
        taskNodes.remove(node.task.getId());

    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Ошибка: передан пустой объект task");
            return;
        }
        if (taskNodes.containsKey(task.getId())) {
            removeNode(taskNodes.get(task.getId()));
        }
        linkTasks.put(task.getId(), task);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (taskNodes.containsKey(id)) {
            removeNode(taskNodes.get(id));
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }
}
