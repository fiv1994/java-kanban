package com.yandex.app.model.test;

import com.yandex.app.model.Subtask;
import com.yandex.app.service.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask;

    @Test
    void getEpicId() {
        assertEquals(123, subtask.getEpicId());
    }

    @Test
    void setEpicId() {
        subtask.setEpicId(456);
        assertEquals(456, subtask.getEpicId());
    }

    @Test
    void testToString() {
        String expected = "com.yandex.app.model.Subtask{" +
                "title='Test Subtask" +
                "', description='Test Description" +
                "', taskId=1, status=TODO" +
                ", epicId=123" +
                '}';
        assertEquals(expected, subtask.toString());
    }

    @Test
    public void testSetSelfAsEpic() {
        Subtask subtask = new Subtask("Test Subtask", "Test Description", 1, TaskStatus.NEW, false, 123);
        subtask.setEpicId(1); // Попытка сделать самого себя эпиком

        assertNotEquals(1, subtask.getEpicId()); // ID эпика не должен измениться
    }
}