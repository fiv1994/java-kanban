package test.com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager taskManager;

    private TaskStatus taskStatus;

    @Test
    public void testCreateAndRetrieveTask() {
        Task task = new Task("Простая задача", "Простое описание", 1, TaskStatus.NEW);
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals("Простая задача", retrievedTask.getTitle());
        assertEquals("Простое описание", retrievedTask.getDescription());
    }

    @Test
    public void testCreateAndUpdateTask() {
        Task task = new Task("Простая задача", "Простое описание", 1, TaskStatus.NEW);
        taskManager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Простая задача", "Простое описание", 1, TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());
        Task removedTask = taskManager.getTaskById(task.getId());
        assertNull(removedTask);
    }

    @Test
    public void testTaskIdConflict() {
        int manuallyAssignedId = 1;
        Task taskWithId = new Task("Задача с ID", "Описание с ID", manuallyAssignedId,
                TaskStatus.NEW);
        Task taskWithGeneratedId = new Task("Задача со сгенерированным ID",
                "Описание со сгенерированным ID", 0, TaskStatus.IN_PROGRESS);

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

    @Test
    public void testTaskImmutability() {
        Task originalTask = new Task("Исходная задача", "Исходное описание", 1, TaskStatus.NEW);
        taskManager.createTask(originalTask);

        // Клонирование исходной задачи
        Task clonedTask = new Task(originalTask.getTitle(), originalTask.getDescription(), originalTask.getId(), originalTask.getStatus());

        // Изменение клонированной задачи
        clonedTask.setTitle("Заголовок клонированной задачи");
        clonedTask.setDescription("Описание клонированной задачи");
        clonedTask.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.createTask(clonedTask);

        Task retrievedOriginalTask = taskManager.getTaskById(originalTask.getId());
        Task retrievedClonedTask = taskManager.getTaskById(clonedTask.getId());

        // Проверка того, что исходная задача осталась без изменений
        assertEquals("Исходная задача", retrievedOriginalTask.getTitle());
        assertEquals("Исходное описание", retrievedOriginalTask.getDescription());
        assertEquals(TaskStatus.NEW, retrievedOriginalTask.getStatus());

        // Проверка добавления клонированной задачи
        assertEquals("Заголовок клонированной задачи", retrievedClonedTask.getTitle());
        assertEquals("Описание клонированной задачи", retrievedClonedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedClonedTask.getStatus());
    }


}
