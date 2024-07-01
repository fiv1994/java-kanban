package com.yandex.app.model;

import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Создание экземпляра InMemoryTaskManager
        task = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.IN_PROGRESS);
    }

    @Test
    void getTitle() {
        assertEquals("Test Task", task.getTitle());
    }

    @Test
    void setTitle() {
        task.setTitle("New Title");
        assertEquals("New Title", task.getTitle());
    }

    @Test
    void getDescription() {
        assertEquals("Test Description", task.getDescription());
    }

    @Test
    void setDescription() {
        task.setDescription("New Description");
        assertEquals("New Description", task.getDescription());
    }

    @Test
    void getId() {
        assertEquals(1, task.getId());
    }

    @Test
    void setId() {
        task.setId(2);
        assertEquals(2, task.getId());
    }

    @Test
    void getStatus() {
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void setStatus() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void testToString() {
        String expected = "com.yandex.app.model.Task{" +
                "title='Test Task" +
                "', description='Test Description" +
                "', id=1, status=IN_PROGRESS" +
                '}';
        assertEquals(expected, task.toString());
    }

    @Test
    void testEquals() {
        Task sameTask = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        Task differentTask = new Task("Different Task", "Different Description", 2, Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.IN_PROGRESS);

        assertTrue(task.equals(sameTask));
        assertFalse(task.equals(differentTask));
    }

    @Test
    void testHashCode() {
        assertEquals(32, task.hashCode());
    }

    @Test
    public void testEqualsForTask() {
        Task task1 = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        Task task2 = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Different Task", "Different Description", 2, Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.IN_PROGRESS);

        assertTrue(task1.equals(task2));
        assertFalse(task1.equals(task3));
    }

    @Test
    void testUpdateTaskIntegrity() {
        Task task = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        task.setId(2); // Обновление id задачи
        assertNotEquals(1, task.getId()); // Проверка, что id изменился
    }
}