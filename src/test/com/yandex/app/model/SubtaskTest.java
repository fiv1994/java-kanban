package test.com.yandex.app.model;

import com.yandex.app.model.Subtask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Создание экземпляра InMemoryTaskManager
        subtask = new Subtask("Test Subtask", "Test Description", 1, TaskStatus.NEW, false, 123);
    }

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
                "', taskId=1, status=NEW" +
                ", epicId=123" +
                '}';
        assertEquals(expected, subtask.toString());
    }

    @Test
    void testSetSelfAsEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Subtask subtask = new Subtask("Test Subtask", "Test Description", 1, TaskStatus.NEW,
                false, 123);
        subtask.setEpicId(1); // Попытка сделать самого себя эпиком

        assertFalse(taskManager.isValidSubtask(subtask));
    }
}