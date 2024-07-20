package com.yandex.app.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Subtask;
import com.yandex.app.server.service.ErrorResponse;
import com.yandex.app.server.service.HttpMethod;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        switch (HttpMethod.valueOf(httpMethod)) {
            case GET -> handleGet(exchange);
            case POST -> handlePost(exchange);
            case DELETE -> handleDelete(exchange);
            default -> sendNotFound(new ErrorResponse("Объект не найден"), exchange, 404);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        Subtask subtask = getTaskManager().getSubtask(id);
        sendText(subtask, exchange, 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = getGson().fromJson(requestBody, Subtask.class);
        Subtask createdSubtask = getTaskManager().createSubtask(subtask);
        sendText(createdSubtask, exchange, 200);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        Subtask subtask = getTaskManager().removeSubtaskById(id);
        sendText(subtask, exchange, 200);
    }
}
