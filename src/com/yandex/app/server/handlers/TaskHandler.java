package com.yandex.app.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.server.service.ErrorResponse;
import com.yandex.app.server.service.HttpMethod;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String httpMethod = exchange.getRequestMethod();
            switch (HttpMethod.valueOf(httpMethod)) {
                case GET -> handleGet(exchange);
                case POST -> handlePost(exchange);
                case DELETE -> handleDelete(exchange);
                default -> sendNotFound(new ErrorResponse("Объект не найден"), exchange, 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(new ErrorResponse("Ошибка работы с задачей"), exchange, 400);
            throw e; // Проброс исключения, чтобы тест не продолжался
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            int id = Integer.parseInt(path.split("/")[2]);
            Task task = getTaskManager().getTaskById(id);
            sendText(task, exchange, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(new ErrorResponse("Ошибка при создании задачи"), exchange, 400);
            throw e; // Проброс исключения, чтобы тест не продолжался
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            // Логирование тела запроса:
            System.out.println("Request Body: " + requestBody);
            // Использование экземпляра Gson, переданного в конструктор:
            Task task = getGson().fromJson(requestBody, Task.class);
            Task createdTask = getTaskManager().createTask(task);
            sendText(createdTask, exchange, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(new ErrorResponse("Задача пересекается с другими"), exchange, 406);
            throw e; // Проброс исключения, чтобы тест не продолжался
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            int id = Integer.parseInt(path.split("/")[2]);
            Task task = getTaskManager().removeTaskById(id);
            sendText(task, exchange, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(new ErrorResponse("Ошибка при удалении задачи"), exchange, 400);
            throw e; // Проброс исключения, чтобы тест не продолжался
        }
    }
}
