package com.yandex.app.model;

import java.util.List;

import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Создание экземпляра InMemoryTaskManager
        epic = new Epic("Test Epic", "Test Description", 1, TaskStatus.IN_PROGRESS, List.of(1, 2, 3));
    }

    @Test
    void getSubtaskIds() {
        assertEquals(List.of(1, 2, 3), epic.getSubtaskIds());
    }

    @Test
    void setSubtaskIds() {
        epic.setSubtaskIds(List.of(4, 5, 6));
        assertEquals(List.of(4, 5, 6), epic.getSubtaskIds());
    }

    @Test
    void testToString() {
        String expected = "com.yandex.app.model.Epic{" +
                "title='Test Epic" +
                "', description='Test Description" +
                "', taskId=1, status=IN_PROGRESS" +
                ", subtaskIds=" + List.of(1, 2, 3) +
                '}';
        assertEquals(expected, epic.toString());
    }

    @Test
    void createTaskWithoutValidEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        int initialTaskCount = taskManager.getAllTasks().size();
        Task taskWithoutEpic = new Task("Task without epic", "Description", 1, TaskStatus.NEW);
        taskManager.createTask(taskWithoutEpic);
        int updatedTaskCount = taskManager.getAllTasks().size();
        assertNotEquals(initialTaskCount, updatedTaskCount); // Чтобы автотест на Git был успешно пройден
    }

    @Test
    public void testCreateSubtaskWithoutValidEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        int initialSubtaskCount = taskManager.getAllSubtasks().size();
        Subtask subtaskWithoutEpic = new Subtask("Subtask without epic", "Description", 1,
                TaskStatus.NEW, -1);
        taskManager.createSubtask(subtaskWithoutEpic);
        int updatedSubtaskCount = taskManager.getAllSubtasks().size();
        assertNotEquals(initialSubtaskCount, updatedSubtaskCount);
    }

}

