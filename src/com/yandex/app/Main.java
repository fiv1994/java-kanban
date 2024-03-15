package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Создание com.yandex.app.service.TaskManager
        TaskManager taskManager = new TaskManager();

        // Создание задач
        Task task1 = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2, TaskStatus.IN_PROGRESS);

        // Создание подзадач для эпика
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1",
                                        3, TaskStatus.NEW, false, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2",
                                        4, TaskStatus.IN_PROGRESS, true, 1);

        // Создание эпиков
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 5,
                                                TaskStatus.NEW, new ArrayList<>());
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", 6,
                                                TaskStatus.IN_PROGRESS, new ArrayList<>());

        // Добавление задач и эпиков в com.yandex.app.service.TaskManager
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(epic1);
        taskManager.createTask(epic2);

        // Управление статусами эпиков и подзадач
        taskManager.manageStatuses();

        // Вывод результатов
        System.out.println("Список всех задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // Удаление задач и эпиков
        taskManager.removeTaskById(2);
        taskManager.removeTaskById(6);
    }
}
