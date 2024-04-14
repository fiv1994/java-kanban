package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;
    private HistoryManager historyManager = Managers.getDefaultHistory();

    private int taskIdCounter = 1;
    private int subtaskIdCounter = 1;
    private int epicIdCounter = 1;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
    }

    @Override
    public int getNextTaskId() {
        return taskIdCounter++;
    }

    @Override
    public int getNextSubtaskId() {
        return subtaskIdCounter++;
    }

    @Override
    public int getNextEpicId() {
        return epicIdCounter++;
    }


    @Override
    public Task getTaskById(int taskId) {
        Task task = taskMap.get(taskId);  // Получение задачи по идентификатору
        if (task != null) {
            historyManager.updateHistory(task);  // Обновление истории просмотров
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = subtaskMap.get(subtaskId); // Полученная подзадача
        if (subtask != null) {
            historyManager.updateHistory(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epicMap.get(epicId); // Полученная подзадача
        if (epic != null) {
            historyManager.updateHistory(epic);
        }
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void createTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
    }

    @Override
    public void createEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    @Override
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

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskMap.containsKey(subtask.getId())) {
            subtaskMap.put(subtask.getId(), subtask);
            taskMap.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
            taskMap.put(epic.getId(), epic);
        }
    }

    @Override
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

    @Override
    public List<Task> getTasks(int taskId) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : taskMap.values()) {
            if (task.getId() == taskId) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            if (subtask.getId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    @Override
    public List<Epic> getEpics(int epicId) {
        List<Epic> epics = new ArrayList<>();
        for (Epic epic: epicMap.values()) {
            if (epic.getId() == epicId) {
                epics.add(epic);
            }
        }
        return epics;
    }

    @Override
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


    @Override
    public void removeAllTasks() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.setSubtaskIds(new ArrayList<>());
            epic.setStatus(TaskStatus.NEW);
            updateTask(epic);
        }
    }

    @Override
    public void removeAllEpics() {
        epicMap.clear();
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.setSubtaskIds(new ArrayList<>());
            epic.setStatus(TaskStatus.NEW);
            updateTask(epic);
        }
    }

    // Методы для работы с историей
    private void addToHistory(Task task) {
        historyManager.add(task);
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isValidSubtask(Subtask subtask) {
        return subtask.getId() != subtask.getEpicId();
    }
}