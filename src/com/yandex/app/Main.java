package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksForEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {
        // Получение объекта-менеджера задач через Managers.getDefault()
        TaskManager taskManager = Managers.getDefault();

        // Использование полученного объекта-менеджера для выполнения операций
        Task task = taskManager.getTaskById(1);
        List<Task> history = taskManager.getHistory();

        // Создание com.yandex.app.service.TaskManager
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        // Создание задач
        Task task1 = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2, TaskStatus.IN_PROGRESS);

        // Создание подзадач для эпика
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1",
                                        3, TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2",
                                        4, TaskStatus.IN_PROGRESS, 1);

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

        // Вывод всех задач и истории после создания задач
        printAllTasks(taskManager);

        // Управление статусами эпиков и подзадач
        taskManager.manageStatuses();

        // Вывод результатов
        System.out.println("Список всех задач:");
        for (Task t : taskManager.getAllTasks()) {
            System.out.println(t);
        }
        for (Subtask st : taskManager.getAllSubtasks()) {
            System.out.println(st);
        }
        for (Epic e : taskManager.getAllEpics()) {
            System.out.println(e);
        }

        // Удаление задач и эпиков
        taskManager.removeTaskById(2);
        taskManager.removeTaskById(6);
    }
}
