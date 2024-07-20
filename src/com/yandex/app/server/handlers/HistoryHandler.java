package com.yandex.app.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.server.service.ErrorResponse;
import com.yandex.app.server.service.HttpMethod;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        switch (HttpMethod.valueOf(httpMethod)) {
            case GET -> handleGet(exchange);
            default -> sendNotFound(new ErrorResponse("Неверный HTTP-метод"), exchange, 404);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> history = getTaskManager().getHistory();
        sendText(history, exchange, 200);
    }
}
