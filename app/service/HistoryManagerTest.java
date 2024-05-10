package test.com.yandex.app.service;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskStatus;

import com.yandex.app.service.HistoryManager;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;

    @Test
    void testAddAndGetHistory() {
        Task task = new Task("Test Task", "Test Description", 1, TaskStatus.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void testRemove() {
        Task task = new Task("Test Task", "Test Description", 1, TaskStatus.NEW);
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testUpdateHistory() {
        Task task = new Task("Test Task", "Test Description", 1, TaskStatus.NEW);
        historyManager.add(task);
        Task updatedTask = new Task("Updated Task", "Updated Description", 1, TaskStatus.IN_PROGRESS);
        historyManager.updateHistory(updatedTask);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(updatedTask, history.get(0));
    }
}