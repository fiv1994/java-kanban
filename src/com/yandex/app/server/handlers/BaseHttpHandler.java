package com.yandex.app.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.server.service.ErrorResponse;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    void sendText(Object body, HttpExchange exchange, int code) throws IOException {
        String responseJson = gson.toJson(body);
        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }

    void sendNotFound(Object body, HttpExchange exchange, int code) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("Объект не найден");
        sendText(errorResponse, exchange, 404);
        return;
    }

    void sendHasInteractions(Object body, HttpExchange exchange, int code) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("Задача пересекается с другими");
        sendText(errorResponse, exchange, 406);
        return;
    }
}