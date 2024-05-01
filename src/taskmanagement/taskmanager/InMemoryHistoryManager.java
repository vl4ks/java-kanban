package taskmanagement.taskmanager;

import taskmanagement.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private HashMap<Integer, Node> taskNodes = new HashMap<>();

    private static class Node {
        Task task;
        Node next;
        Node previous;

        Node(Task task) {
            this.task = task;
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
        }
        tail = newNode;
        taskNodes.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            System.out.println("Ошибка: передан пустой узел");
            return;
        }
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
            return;
        }
        removeNode(taskNodes.get(task.getId()));
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(taskNodes.get(id));
    }

    private List<Task> getTasks() {
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
        return getTasks();
    }
}
