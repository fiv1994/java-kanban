package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private TreeSet<Task> prioritizedTasks;
    private TreeSet<Subtask> prioritizedSubtasks;

    private int taskIdCounter = 1;
    private int subtaskIdCounter = 1;
    private int epicIdCounter = 1;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
        prioritizedSubtasks = new TreeSet<>(new SubtaskStartTimeComparator());
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private static class TaskStartTimeComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime() == null || t2.getStartTime() == null) {
                return 0;
            }
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    }

    private static class SubtaskStartTimeComparator implements Comparator<Subtask> {
        @Override
        public int compare(Subtask s1, Subtask s2) {
            if (s1.getStartTime() == null || s2.getStartTime() == null) {
                return 0;
            }
            return s1.getStartTime().compareTo(s2.getStartTime());
        }
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getEndTime().isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(task2.getEndTime())) {
            return false;
        }
        return true;
    }

    private void updatePrioritizedTasks(Task task) {
        // Удаляем старую версию задачи, если она уже есть в TreeSet
        prioritizedTasks.removeIf(existingTask -> existingTask.getId() == task.getId());

        // Добавляем новую версию задачи
        prioritizedTasks.add(task);

        // Проверяем, не нарушает ли добавление порядка задач
        assert isTasksSorted() : "Порядок задач нарушен после обновления";
    }

    private boolean isTasksSorted() {
        Task previous = null;
        for (Task task : prioritizedTasks) {
            TaskStartTimeComparator comparator = new TaskStartTimeComparator();
            if (previous != null && comparator.compare(previous, task) > 0) {
                return false;
            }
            previous = task;
        }
        return true;
    }

    private void updatePrioritizedSubtasks(Subtask subtask) {
        // Удаляем старую версию подзадачи, если она уже есть в TreeSet
        prioritizedSubtasks.removeIf(existingSubtask -> existingSubtask.getId() == subtask.getId());

        // Добавляем новую версию задачи
        prioritizedSubtasks.add(subtask);

        // Проверяем, не нарушает ли добавление порядка задач
        assert isSubtasksSorted() : "Порядок задач нарушен после обновления";
    }

    private boolean isSubtasksSorted() {
        Subtask previous = null;
        for (Subtask subtask : prioritizedSubtasks) {
            SubtaskStartTimeComparator comparator = new SubtaskStartTimeComparator();
            if (previous != null && comparator.compare(previous, subtask) > 0) {
                return false;
            }
            previous = subtask;
        }
        return true;
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
        // Проверяем, не пересекается ли новая задача с уже существующими
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(task, existingTask));
        if (isOverlapping) {
            System.out.println("Задача пересекается по времени выполнения с другой задачей.");
        } else {
            taskMap.put(task.getId(), task);
            // Обновляем TreeSet с приоритетными задачами, если это необходимо
            updatePrioritizedTasks(task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        // Проверяем, не пересекается ли новая задача с уже существующими
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingSubtask -> isOverlapping(subtask, existingSubtask));
        if (isOverlapping) {
            System.out.println("Задача пересекается по времени выполнения с другой задачей.");
        } else {
            subtaskMap.put(subtask.getId(), subtask);
            // Обновляем TreeSet с приоритетными задачами, если это необходимо
            updatePrioritizedSubtasks(subtask);
        }
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
        return taskMap.values().stream()
                .filter(task -> task.getId() == taskId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        return subtaskMap.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Epic> getEpics(int epicId) {
        return epicMap.values().stream()
                .filter(epic -> epic.getId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public void manageStatuses() {
        epicMap.values().forEach(epic -> {
            List<Subtask> subtasks = getSubtasksForEpic(epic.getId());
            TaskStatus status = subtasks.stream()
                    .map(Subtask::getStatus)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toSet(),
                            statuses -> {
                                if (statuses.size() == 1) {
                                    return statuses.contains(TaskStatus.NEW) ? TaskStatus.NEW : TaskStatus.DONE;
                                }
                                return TaskStatus.IN_PROGRESS;
                            }
                    ));
            epic.setStatus(status);
            updateTask(epic);
        });
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