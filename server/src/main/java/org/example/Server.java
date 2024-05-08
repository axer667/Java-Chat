package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    private final int port; // Порт
    boolean stop = false; // Если нам потребуется остановить сервер

    public List<ClientThread> connectionList = Collections.synchronizedList(new ArrayList<>()); // список соединений

    public Server (int port) {
        this.port = port;
    }

    public void start() {
        try {
            try (ServerSocket server = new ServerSocket(this.port)) {
                System.out.println("Сервер запущен на порту: " + port);
                while (true) {
                    if (this.stop) {
                        System.out.println("Сервер принудительно остановлен.");
                        server.close();
                        break;
                    }
                    Socket socket = server.accept(); // блокируем до возникновения нового соединения
                    ClientThread ss = new ClientThread(socket, this); // создаем новое соединение
                    connectionList.add(ss); // добавиляем соединенние в список
                    System.out.print("Клиент присоединился: ");
                    System.out.println(ss.getConnection());

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeClient(ClientThread client) {
        System.out.print("Клиент отсоединился: ");
        System.out.println(client.getConnection());
        connectionList.remove(client);
    }
}
