package com.yandex.app.server;

import com.google.gson.Gson;
import com.yandex.app.server.service.GsonProvider;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;
    private int port;

    @BeforeEach
    public void setUp() throws IOException {
        try {
            taskManager = new InMemoryTaskManager();
            gson = new GsonProvider().getGson(); // Использование экземпляра Gson
            port = 8080;
            server = new HttpTaskServer(taskManager, gson, port);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Проброс исключения, чтобы тест не продолжался
        }
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        try {
            // Создание и отправка запроса на добавление задачи
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"Test Task\"}"))
                    .build();
            System.out.println("Отправка запроса на добавление задачи"); // Добавление логов
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Проверка статуса ответа
            assertEquals(200, response.statusCode());

            // Проверка, что задача добавлена в менеджер
            assertEquals(1, taskManager.getAllTasks().size());
            assertEquals("Test Task", taskManager.getAllTasks().get(0).getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Проброс исключения, чтобы тест не продолжался
        }
    }
}
