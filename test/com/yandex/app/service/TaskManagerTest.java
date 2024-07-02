package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager taskManager;

    private TaskStatus taskStatus;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager(); // Инициализация объекта TaskManager
        taskStatus = TaskStatus.NEW; // Инициализация объекта TaskStatus
    }

    @Test
    public void testCreateAndRetrieveTask() {
        Task task = new Task("Простая задача", "Простое описание", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals("Простая задача", retrievedTask.getTitle());
        assertEquals("Простое описание", retrievedTask.getDescription());
    }

    @Test
    public void testCreateAndUpdateTask() {
        Task task = new Task("Простая задача", "Простое описание", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        taskManager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Простая задача", "Простое описание", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());
        Task removedTask = taskManager.getTaskById(task.getId());
        assertNull(removedTask);
    }

    @Test
    public void testTaskIdConflict() {
        int manuallyAssignedId = 1;
        Task taskWithId = new Task("Задача с ID", "Описание с ID", manuallyAssignedId,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(60), TaskStatus.NEW);
        Task taskWithGeneratedId = new Task("Задача со сгенерированным ID",
                "Описание со сгенерированным ID", 8, Duration.ofMinutes(5),
                LocalDateTime.now().plusMinutes(30), TaskStatus.IN_PROGRESS);

        taskManager.createTask(taskWithId);
        taskManager.createTask(taskWithGeneratedId);

        Task foundTaskWithId = taskManager.getTaskById(manuallyAssignedId);
        Task foundTaskWithGeneratedId = taskManager.getAllTasks().stream()
                .filter(task -> task.getId() != manuallyAssignedId) // Находим задачу со сгенерированным id
                .findFirst().orElse(null);

        assertNotNull(foundTaskWithId);
        assertNotNull(foundTaskWithGeneratedId);
        assertNotSame(foundTaskWithId, foundTaskWithGeneratedId);
    }

    public boolean isSubtaskLinkedToEpic(Subtask subtask) {
        // Получаем ID эпика, связанного с подзадачей
        Integer epicId = subtask.getEpicId();

        // Проверяем, существует ли эпик с таким ID
        Epic epic = taskManager.getEpic(epicId);

        // Возвращаем true, если эпик существует, иначе false
        return epic != null;
    }

    @Test
    public void epicStatusWithAllNewSubtasks() {
        // Создаем эпик без известного статуса и списка идентификаторов подзадач
        Epic epic = new Epic("Epic Title", "Epic Description", 1, null, new ArrayList<>());
        taskManager.createEpic(epic);

        // Создаем подзадачи с статусом NEW и добавляем их в taskManager
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 2, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 3, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.NEW);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Добавляем ID подзадач в список идентификаторов эпика
        epic.getSubtaskIds().add(subtask1.getId());
        epic.getSubtaskIds().add(subtask2.getId());

        // Получаем список подзадач для эпика
        List<Subtask> subtasks = taskManager.getSubtasksForEpic(epic.getId());

        // Обновляем статус эпика на основе статусов подзадач
        epic.updateStatusBasedOnSubtasks(subtasks);

        // Обновляем эпик в taskManager
        taskManager.updateEpic(epic);

        // Получаем обновленный эпик, проверяем его статус и связность
        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(TaskStatus.NEW, retrievedEpic.getStatus());
        assertTrue(isSubtaskLinkedToEpic(subtask1));
        assertTrue(isSubtaskLinkedToEpic(subtask2));
    }

    @Test
    public void epicStatusWithAllDoneSubtasks() {
        Epic epic = new Epic("Epic Title", "Epic Description", 1, null, new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 2, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 3, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.DONE);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        epic.getSubtaskIds().add(subtask1.getId());
        epic.getSubtaskIds().add(subtask2.getId());

        List<Subtask> subtasks = taskManager.getSubtasksForEpic(epic.getId());

        epic.updateStatusBasedOnSubtasks(subtasks);

        taskManager.updateEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(TaskStatus.DONE, retrievedEpic.getStatus());
        assertTrue(isSubtaskLinkedToEpic(subtask1));
        assertTrue(isSubtaskLinkedToEpic(subtask2));
    }

    @Test
    public void epicStatusWithNewAndDoneSubtasks() {
        Epic epic = new Epic("Epic Title", "Epic Description", 1, null, new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 2, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 3, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.DONE);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        epic.getSubtaskIds().add(subtask1.getId());
        epic.getSubtaskIds().add(subtask2.getId());

        List<Subtask> subtasks = taskManager.getSubtasksForEpic(epic.getId());

        epic.updateStatusBasedOnSubtasks(subtasks);

        taskManager.updateEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedEpic.getStatus());
        assertTrue(isSubtaskLinkedToEpic(subtask1));
        assertTrue(isSubtaskLinkedToEpic(subtask2));
    }

    @Test
    public void epicStatusWithSubtasksInProgress() {
        Epic epic = new Epic("Epic Title", "Epic Description", 1, null, new ArrayList<>());
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 2, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 3, epic.getId(), Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.IN_PROGRESS);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        epic.getSubtaskIds().add(subtask1.getId());
        epic.getSubtaskIds().add(subtask2.getId());

        List<Subtask> subtasks = taskManager.getSubtasksForEpic(epic.getId());

        epic.updateStatusBasedOnSubtasks(subtasks);

        taskManager.updateEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedEpic.getStatus());
        assertTrue(isSubtaskLinkedToEpic(subtask1));
        assertTrue(isSubtaskLinkedToEpic(subtask2));
    }



    @Test
    public void testTaskImmutability() {
        Task originalTask = new Task("Исходная задача", "Исходное описание", 1, Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.NEW);
        taskManager.createTask(originalTask);

        // Клонирование исходной задачи
        Task clonedTask = new Task(originalTask.getTitle(), originalTask.getDescription(), originalTask.getId(),
                originalTask.getDuration(), originalTask.getStartTime(), originalTask.getStatus());

        // Изменение клонированной задачи
        clonedTask.setTitle("Заголовок клонированной задачи");
        clonedTask.setDescription("Описание клонированной задачи");
        clonedTask.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateTask(clonedTask); // Обновление клонированной задачи в taskManager

        Task retrievedOriginalTask = taskManager.getTaskById(originalTask.getId());
        Task retrievedClonedTask = taskManager.getTaskById(clonedTask.getId());

        // Проверка добавления клонированной задачи
        assertEquals("Заголовок клонированной задачи", retrievedClonedTask.getTitle());
        assertEquals("Описание клонированной задачи", retrievedClonedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedClonedTask.getStatus());
    }

}
