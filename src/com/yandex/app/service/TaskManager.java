package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    int getNextTaskId();

    int getNextSubtaskId();

    int getNextEpicId();

    Task getTaskById(int taskId);

    Subtask getSubtask(int subtaskId);

    Epic getEpic(int epicId);

    List<Task> getHistory();

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    Task createTask(Task task);

    Subtask createSubtask(Subtask subtask);

    Epic createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    Task removeTaskById(int taskId);

    Subtask removeSubtaskById(int subtaskId);

    Epic removeEpicById(int epicId);

    List<Task> getTasks(int taskId);

    List<Subtask> getSubtasksForEpic(int epicId);

    List<Epic> getEpics(int epicId);

    void manageStatuses();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Set<Task> getPrioritizedTasks();
}