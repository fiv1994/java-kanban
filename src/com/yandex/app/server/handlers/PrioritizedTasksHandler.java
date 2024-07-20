package com.yandex.app.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.server.service.ErrorResponse;
import com.yandex.app.server.service.HttpMethod;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritizedTasksHandler extends BaseHttpHandler {

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        switch (HttpMethod.valueOf(httpMethod)) {
            case GET -> handleGet(exchange);
            default -> sendNotFound(new ErrorResponse("Объект не найден"), exchange, 404);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = getTaskManager().getPrioritizedTasks();
        sendText(prioritizedTasks, exchange, 200);
    }
}
