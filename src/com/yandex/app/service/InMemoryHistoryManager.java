package com.yandex.app.service;

import com.yandex.app.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();

    private static class Node {
        int taskId;
        Node prev;
        Node next;

        Node(int taskId) {
            this.taskId = taskId;
            this.prev = null;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private HashMap<Integer, Node> map;

    public InMemoryHistoryManager() {
        this.head = null;
        this.tail = null;
        this.map = new HashMap<>();
    }

    private void linkLast(int taskId) {
        Node newNode = new Node(taskId);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
    }

    private List<Integer> getTasks() {
        List<Integer> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.taskId);
            current = current.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        map.remove(node.taskId);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (map.containsKey(task.getId())) {
                removeNode(map.get(task.getId()));
            }
            linkLast(task.getId());
            map.put(task.getId(), tail);
            history.add(task); // Добавляем задачу в список history
        } else {
            System.out.println("Попытка добавления несуществующей задачи в историю");
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyCopy = new ArrayList<>();
        for (Task task : history) {
            historyCopy.add(task);
        }
        return historyCopy;
    }

    @Override
    public void updateHistory(Task task) {
        if (task != null) {
            remove(task.getId()); // Удаляем старую версию задачи
            add(task); // Добавляем новую версию задачи в конец истории
        }
    }


    @Override
    public void remove(int id) {
        Task taskToRemove = null;
        for (Task task : history) {
            if (task.getId() == id) {
                taskToRemove = task;
                break;
            }
        }
        if (taskToRemove != null) {
            history.remove(taskToRemove); // Удаляем задачу из списка history
        }
    }


}

