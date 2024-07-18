package com.yandex.app.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Epic;
import com.yandex.app.server.service.ErrorResponse;
import com.yandex.app.server.service.HttpMethod;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        switch (HttpMethod.valueOf(httpMethod)) {
            case GET -> handleGet(exchange);
            case POST -> handlePost(exchange);
            case DELETE -> handleDelete(exchange);
            default -> sendNotFound(new ErrorResponse("Неверный HTTP-метод"), exchange, 404);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        Epic epic = getTaskManager().getEpic(id);
        sendText(epic, exchange, 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = getGson().fromJson(requestBody, Epic.class);
        Epic createdEpic = getTaskManager().createEpic(epic);
        sendText(createdEpic, exchange, 200);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        Epic epic = getTaskManager().removeEpicById(id);
        sendText(epic, exchange, 200);
    }
}
