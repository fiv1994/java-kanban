package com.yandex.app.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.server.handlers.*;
import com.yandex.app.server.service.GsonProvider;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));
        httpServer.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Gson gson = new GsonProvider().getGson(); // Использование экземпляра Gson из нового конструктора GsonProvider
        HttpTaskServer server = new HttpTaskServer(taskManager, gson);
        server.start();
    }
}
