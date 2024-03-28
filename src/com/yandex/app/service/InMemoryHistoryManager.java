package com.yandex.app.service;

import com.yandex.app.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
        } else {
            System.out.println("Попытка добавления несуществующей задачи в историю");
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // Возвращаем копию истории просмотров
    }
    @Override
    public void updateHistory(Task task) {
        // Удаление дубликатов
        for (Task t : new ArrayList<>(history)) {
            if (t.getId() == task.getId()) {
                history.remove(t);
            }
        }

        history.add(task);  // Добавление задачи в конец истории

        if (history.size() > 10) {
            history = history.subList(history.size() - 10, history.size());  // Обрезка истории до 10 элементов
        }
    }
}