package com.yandex.app.service;

import com.yandex.app.model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddAndFindTask() {
        Task task1 = new Task("Task 1", "Description 1", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, Duration.ofMinutes(3), LocalDateTime.now(),
                TaskStatus.IN_PROGRESS);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Task foundTask1 = taskManager.getTaskById(1);
        Task foundTask2 = taskManager.getTaskById(2);

        assertNotNull(foundTask1);
        assertNotNull(foundTask2);
        assertNotSame(foundTask1, foundTask2); // Проверяем, что это разные задачи
        assertEquals("Task 1", foundTask1.getTitle());
        assertEquals("Description 1", foundTask1.getDescription());
        assertEquals(TaskStatus.NEW, foundTask1.getStatus());
        assertEquals("Task 2", foundTask2.getTitle());
        assertEquals("Description 2", foundTask2.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, foundTask2.getStatus());
    }
}
