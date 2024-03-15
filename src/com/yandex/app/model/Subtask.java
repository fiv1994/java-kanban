package com.yandex.app.model;

import com.yandex.app.service.TaskStatus;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int taskId, TaskStatus status, boolean completed, int epicId) {
        super(title, description, taskId, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
