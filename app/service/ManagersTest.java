package test.com.yandex.app.service;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.InMemoryHistoryManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }
}