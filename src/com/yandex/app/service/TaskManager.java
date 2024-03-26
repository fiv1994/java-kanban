package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.List;

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

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int taskId);

    List<Task> getTasks(int taskId);

    List<Subtask> getSubtasksForEpic(int epicId);

    List<Epic> getEpics(int epicId);

    void updateHistory(Task task);

    void manageStatuses();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();
}
