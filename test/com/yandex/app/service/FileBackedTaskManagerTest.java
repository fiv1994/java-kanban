package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.ManagerLoadException;
import com.yandex.app.service.ManagerSaveException;
import com.yandex.app.service.TaskStatus;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        try {
            tempFile = File.createTempFile("task_manager_test", ".csv");
            manager = new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void saveEmptyFile() {
        manager.save();
        assertTrue(tempFile.exists());
        assertEquals(0, tempFile.length());
    }

    @Test
    void loadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNotNull(loadedManager);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
    }

    @Test
    void saveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.IN_PROGRESS);

        manager.createTask(task1);
        manager.createTask(task2);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNotNull(loadedManager);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
    }

    @Test
    void saveAndLoadMultipleTasksAndSubtasks() {
        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.IN_PROGRESS);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", 11,
                                        TaskStatus.DONE, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", 12,
                                        TaskStatus.NEW, 1);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNotNull(loadedManager);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));

        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertEquals(2, subtasks.size());
        assertEquals(subtask1, subtasks.get(0));
        assertEquals(subtask2, subtasks.get(1));
    }

    @Test
    void saveAndLoadInvalidFileThrowsException() {
        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(new File("invalid_file.csv"));
        });
    }

    @Test
    void saveToFileIOExceptionThrowsException() {
        manager.createTask(new Task("Task 1", "Description 1", 1, TaskStatus.NEW));
        tempFile.setReadOnly();

        assertThrows(ManagerSaveException.class, manager::save);
    }
}