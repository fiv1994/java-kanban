package com.yandex.app.model;

import com.yandex.app.service.TaskStatus;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;

    public Epic(String title, String description, int taskId, TaskStatus status, List<Integer> subtaskIds) {
        super(title, description, taskId, status);
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
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
