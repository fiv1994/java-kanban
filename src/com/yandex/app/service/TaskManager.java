package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;

    private int taskIdCounter = 0;
    private int subtaskIdCounter = 0;
    private int epicIdCounter = 0;

    public int getNextTaskId() {
        return taskIdCounter++;
    }

    public int getNextSubtaskId() {
        return subtaskIdCounter++;
    }

    public int getNextEpicId() {
        return epicIdCounter++;
    }

    public TaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        taskIdCounter = 1;
    }

    public Task getTaskById(int taskId) {
        return taskMap.get(taskId);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public void createTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
    }

    public void createEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
            if (task.getClass().equals(Epic.class)) {
                epicMap.put(task.getId(), (Epic) task);
            } else if (task.getClass().equals(Subtask.class)) {
                subtaskMap.put(task.getId(), (Subtask) task);
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskMap.containsKey(subtask.getId())) {
            subtaskMap.put(subtask.getId(), subtask);
            taskMap.put(subtask.getId(), subtask);
        }
    }

    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
            taskMap.put(epic.getId(), epic);
        }
    }

    public void removeTaskById(int taskId) {
        Task task = taskMap.get(taskId);
        if (task != null) {
            if (task.getClass().equals(Epic.class)) {
                Epic epic = (Epic) task;
                List<Subtask> subtasksToRemove = getSubtasksForEpic(epic.getId());
                for (Subtask subtask : subtasksToRemove) {
                    subtaskMap.remove(subtask.getId());
                    taskMap.remove(subtask.getId());
                }
                epicMap.remove(taskId);
            } else if (task.getClass().equals(Subtask.class)) {
                subtaskMap.remove(taskId);
            }
            taskMap.remove(taskId);
        }
    }

    public List<Task> getTasks(int taskId) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : taskMap.values()) {
            if (task.getId() == taskId) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            if (subtask.getId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    public List<Epic> getEpics(int epicId) {
        List<Epic> epics = new ArrayList<>();
        for (Epic epic: epicMap.values()) {
            if (epic.getId() == epicId) {
                epics.add(epic);
            }
        }
        return epics;
    }

    public void manageStatuses() {
        for (Epic epic : epicMap.values()) {
            List<Subtask> subtasks = getSubtasksForEpic(epic.getId());
            boolean allNew = true;
            boolean allDone = true;

            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() != TaskStatus.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
            }

            if (allNew) {
                epic.setStatus(TaskStatus.NEW);
            } else if (allDone) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
            updateTask(epic);
        }
    }


    public void removeAllTasks() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }

    public void removeAllSubtasks() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.setSubtaskIds(new ArrayList<>());
            epic.setStatus(TaskStatus.NEW);
            updateTask(epic);
        }
    }

    public void removeAllEpics() {
        epicMap.clear();
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.setSubtaskIds(new ArrayList<>());
            epic.setStatus(TaskStatus.NEW);
            updateTask(epic);
        }
    }
}