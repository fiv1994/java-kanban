package com.yandex.app.model;

import com.yandex.app.service.TaskStatus;
import com.yandex.app.service.TaskType;

import java.util.Objects;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String title;
    private String description;
    private int id;
    private TaskStatus status;
    private int epicId;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String title, String description, int id, Duration duration, LocalDateTime startTime,
                TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public Duration getDuration() {
        return duration;
    }

    public long getDurationInMinutes() {
        return duration.toMinutes();
    }

    public void setDurationInMinutes(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(getDurationInMinutes());
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}