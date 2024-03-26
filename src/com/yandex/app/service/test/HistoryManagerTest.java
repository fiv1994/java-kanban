package com.yandex.app.service.test;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskStatus;

import com.yandex.app.service.HistoryManager;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;
    
    @Test
    public void testTaskHistory() {
        Task originalTask = new Task("Исходная задача", "Исходное описание", 0, TaskStatus.NEW);
        historyManager.add(originalTask);

        // Modifying the Исходная задача
        originalTask.setTitle("Изменённая задача");
        originalTask.setDescription("Изменённое описание");

        historyManager.add(originalTask);

        // Retrieving the history
        List<Task> taskHistory = historyManager.getHistory();

        // Checking the history
        assertEquals(2, taskHistory.size());
        assertEquals("Исходная задача", taskHistory.get(0).getTitle());
        assertEquals("Исходное описание", taskHistory.get(0).getDescription());
        assertEquals("Изменённая задача", taskHistory.get(1).getTitle());
        assertEquals("Изменённое описание", taskHistory.get(1).getDescription());
    }
}
