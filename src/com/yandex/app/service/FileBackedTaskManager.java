package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        super();
        this.saveFile = saveFile;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    public void save() {
        try (Writer writer = new BufferedWriter(new FileWriter(saveFile))) {
            String csvData = generateCSVData();
            if (!Files.exists(saveFile.toPath())) {
                Files.createFile(saveFile.toPath());
            }
            // Files.writeString(saveFile.toPath(), csvData);
            writer.write(csvData);
            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи в файл: " + e.getMessage());
        }
    }

    // Статический метод для загрузки данных менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        String filePath = file.getAbsolutePath();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        // Объявляем HashMap для хранения связей эпиков и подзадач
        HashMap<Integer, List<Integer>> epicSubtaskMap = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            String currentType = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Задачи:") || line.startsWith("Подзадачи:") || line.startsWith("Эпики:")) {
                    currentType = line.substring(0, line.length() - 1); // Убираем ":" из названия типа
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 6) { // Проверка на минимальное количество элементов
                    continue; // Пропускаем строку, если элементов недостаточно
                }
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String description = parts[2];
                int epicId = Integer.parseInt(parts[3]);
                Duration duration = Duration.parse(parts[4]);
                LocalDateTime startTime = LocalDateTime.parse(parts[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                TaskStatus status = parseStatus(parts[6]); // Преобразование строки в TaskStatus
                // Получаем список ID подзадач для текущего эпика из HashMap

                List<Integer> subtaskIds = epicSubtaskMap.getOrDefault(epicId, new ArrayList<>());
                if (currentType.equals("Задачи")) {
                    Task task = new Task(title, description, id, duration, startTime, status);
                    manager.createTask(task);
                } else if (currentType.equals("Подзадачи")) {
                    Subtask subtask = new Subtask(title, description, id, epicId, duration, startTime, status);
                    manager.createSubtask(subtask);
                    // Добавляем ID подзадачи в список
                    subtaskIds.add(id);
                    // Обновляем связи эпика и подзадачи в HashMap
                    epicSubtaskMap.put(epicId, subtaskIds);
                } else if (currentType.equals("Эпики")) {
                    Epic epic = new Epic(title, description, id, status, subtaskIds);
                    manager.createEpic(epic);
                }
            }

        } catch (IOException | NumberFormatException e) {
            throw new ManagerLoadException("Ошибка загрузки данных из файла: " + e.getMessage());
        }
        manager.updateEpicStatuses();
        return manager;
    }

    private void updateEpicStatuses() {
        for (Epic epic : getAllEpics()) {
            updateEpic(epic);
        }
    }

    private static TaskStatus parseStatus(String status) {
        switch (status.trim()) { // удаляем лишние пробелы
            case "NEW":
                return TaskStatus.NEW;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "DONE":
                return TaskStatus.DONE;
            default:
                throw new IllegalArgumentException("Недопустимый статус задачи: " + status);
        }
    }

    private String generateCSVData() {
        StringBuilder csvData = new StringBuilder();
        List<Task> allTasks = getAllTasks();
        List<Subtask> allSubtasks = getAllSubtasks();
        List<Epic> allEpics = getAllEpics();

        if (!allTasks.isEmpty()) {
            csvData.append("Задачи:").append(System.lineSeparator());
            for (Task task : allTasks) {
                csvData.append(task.getId()).append(",")
                        .append(task.getTitle().replace(",", "\\,")).append(",")
                        .append(task.getDescription().replace(",", "\\,")).append(",")
                        .append(task.getEpicId()).append(",")
                        .append(task.getDuration()).append(",")
                        .append(task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(",")
                        .append(task.getStatus())
                        .append(System.lineSeparator());
            }
        }

        if (!allSubtasks.isEmpty()) {
            csvData.append("Подзадачи:").append(System.lineSeparator());
            for (Subtask subtask : allSubtasks) {
                csvData.append(subtask.getId()).append(",")
                        .append(subtask.getTitle().replace(",", "\\,")).append(",")
                        .append(subtask.getDescription().replace(",", "\\,")).append(",")
                        .append(subtask.getEpicId()).append(",")
                        .append(subtask.getDuration()).append(",")
                        .append(subtask.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(",")
                        .append(subtask.getStatus())
                        .append(System.lineSeparator());
            }
        }

        if (!allEpics.isEmpty()) {
            csvData.append("Эпики:").append(System.lineSeparator());
            for (Epic epic : allEpics) {
                csvData.append(epic.getId()).append(",")
                        .append(epic.getTitle().replace(",", "\\,")).append(",")
                        .append(epic.getDescription().replace(",", "\\,")).append(",")
                        .append(epic.getStatus()).append(",")
                        .append(epic.getSubtaskIds().toString().replace("", ""))
                        .append(System.lineSeparator());
            }
        }
        return csvData.toString();
    }


}