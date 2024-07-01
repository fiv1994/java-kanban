package com.yandex.app.model;

import com.yandex.app.service.TaskStatus;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskType;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String title, String description, int taskId, TaskStatus status, List<Integer> subtaskIds) {
        super(title, description, taskId, Duration.ZERO, LocalDateTime.MIN, status);
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = taskManager.getSubtask(subtaskId);
            if (subtask != null) {
                taskManager.updateSubtask(subtask);
            }
        }
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void calculateEpicTimes(InMemoryTaskManager taskManager) {
        List<Subtask> subtasks = subtaskIds.stream()
                .map(taskManager::getSubtask)
                .collect(Collectors.toList());

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        LocalDateTime endTime = subtasks.stream()
                .map(subtask -> subtask.getStartTime().plusMinutes(subtask.getDurationInMinutes()))
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        long totalMinutes = subtasks.stream()
                .mapToLong(Subtask::getDurationInMinutes)
                .sum();

        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setDurationInMinutes(totalMinutes);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }

}