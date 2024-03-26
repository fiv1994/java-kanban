package com.yandex.app.model.test;

import java.util.List;

import com.yandex.app.model.Epic;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private TaskManager taskManager;

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
    public void testAddSelfToSubtasks() {
        Epic epic = new Epic("Test Epic", "Test Description", 1, TaskStatus.NEW, List.of(2, 3));
        taskManager.createEpic(epic); // Попытка добавить эпик в экземпляр TaskManager

        assertNull(taskManager.getEpic(1)); // Эпик не должен быть добавлен в TaskManager
    }
}

