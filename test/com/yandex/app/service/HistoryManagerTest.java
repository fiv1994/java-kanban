package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddAndGetHistory() {
        Task task = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void testRemove() {
        Task task = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testUpdateHistory() {
        Task task = new Task("Test Task", "Test Description", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        historyManager.add(task);
        Task updatedTask = new Task("Updated Task", "Updated Description", 1, Duration.ZERO,
                LocalDateTime.MIN, TaskStatus.IN_PROGRESS);
        historyManager.updateHistory(updatedTask);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(updatedTask, history.get(0));
    }

    @Test
    public void shouldHandleEmptyHistoryCorrectly() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldHandleDuplicateEntriesInHistory() {
        Task task1 = new Task("Task 1", "Description 1", 1, Duration.ZERO,
                LocalDateTime.now().plusMinutes(100), TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, Duration.ZERO,
                LocalDateTime.now().plusMinutes(50), TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Добавляем task1 повторно

        List<Task> history = historyManager.getHistory();
        int firstIndex = history.indexOf(task1);
        int lastIndex = history.lastIndexOf(task1);

        assertEquals(firstIndex, lastIndex); // Проверяем, что запись в истории не дублируется
    }

    @Test
    public void shouldHandleDeletionFromHistoryAtDifferentPositions() {
        Task task1 = new Task("Task 1", "Description 1", 1, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);
        Task task3 = new Task("Task 3", "Description 3", 3, Duration.ZERO, LocalDateTime.MIN,
                TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId()); // Удаление из середины
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task3));
    }

}