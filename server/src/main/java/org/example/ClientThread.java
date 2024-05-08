package org.example;


import com.google.gson.Gson;
import org.json.JSONObject;

import javax.swing.Timer;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;


public class ClientThread extends Thread {

    private final Server server; // Собственно говоря, сервер =)
    private final Socket socket; // Сокет, через который сервер общается с клиентом
    private BufferedReader in; // Поток чтения из сокета
    private BufferedWriter out; // Поток записи в сокет

    private final static int DELAY = 5000; // Интервал пинга в мс
    private int inPacks = 0; // Количество полученных пакетов
    private int outPacks = 0; // Количество отправленных пакетов
    private boolean flag = false; // Метка для прерывания чтения
    private Timer timer; // Таймер нам нужен, чтобы пинговать клиента
    private String login; // Будем хранить логин клиента, которого обслуживаем
    private static int clientsCount = 0; // Будем сами хранить количество своих экземпляров

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clientsCount++;
            start(); // вызываем run()
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            //Запускаем таймер
            this.timer = new Timer(DELAY, e -> {
                try {
                    if (inPacks == outPacks) {  //Если количество входящих пакетов от клиента рано исходящему, значит клиент на связи
                        Message pingMessage = new Message("ping", "ping", MessageType.PING);
                        send(new Gson().toJson(pingMessage));
                        outPacks++; // Отправили пакет
                    } else {
                        throw new SocketException();
                    }
                } catch (SocketException se) {
                    System.out.println("packages not clash");
                    System.out.println(login + " disconnected!");
                    //Удаляем клиента из списка доступных и информируем всех
                    try {
                        this.socket.close();
                        this.server.removeClient(this);
                        clientsCount--;
                        Message message = new Message("System-Bot", "Клиент " + login + " отсоединился.", clientsCount, MessageType.END);
                        sendToEveryone(new Gson().toJson(message));
                        flag = true;
                        timer.stop();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            timer.start();

            // Пошли в бесконечность
            while (true) {

                StringBuilder sb = new StringBuilder(); // будем по кусочку собирать строку нашего будущего json
                int c = in.read();

                // Клиент, видать, отвалился... поток прервался
                if (c == -1 || flag) {
                    flag = false;
                    break;
                }

                while(c!=-1){
                    if (c == 10173) { // это наш спец.символ, означающий конец сообщения
                        break;
                    }
                    char ch = (char)c;
                    sb.append(ch);
                    c = in.read();
                }

                String word = sb.toString();
                JSONObject jsonObject = new JSONObject(word); // У нас же не просто строка ;)

                boolean toSend = true; // Будем или нет слать сообщения (есть ведь и служебные)

                if (Objects.equals(jsonObject.getString("type"), "HELLO")) { // Это мы встретили своего юзера
                    login = jsonObject.getString("login");
                } else if (Objects.equals(jsonObject.getString("type"), "END")) {  // Прощальное сообщение
                    threadDead();
                } else if (Objects.equals(jsonObject.getString("type"), "PING")) {
                    toSend = false;
                    this.inPacks++; // Получили пакет
                }

                if (toSend) {
                    // Иногда... не всегда, но нам нужно отправлять дополнительную информацию в сообщении.
                    // в данном случае, отправляем количество клиентов в чате
                    MessageType mt = MessageType.valueOf(jsonObject.getString("type"));
                    Message message = new Message(jsonObject.getString("login"), jsonObject.getString("message"), mt);
                    message.setClientsCount(clientsCount);
                    word = new Gson().toJson(message);
                    sendToEveryone(word);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Отправка сообщения
    private void send(String message) {
        try {
            out.write(message + "➽"); // Стрелочка - спец.символ, конец сообщения
            out.flush();
        } catch (IOException ignored) {}
    }

    // Отправка сообщения всем подкобченным кликетам
    private void sendToEveryone(String message) {
        for (ClientThread ct : server.connectionList) {
            ct.send(message);
        }
    }

    public Socket getConnection () {
        return this.socket;
    }

    // Конец потока. Хватит на сегодня
    private void threadDead() throws IOException {
        timer.stop();
        this.socket.close();
        this.server.removeClient(this);
        clientsCount--;
    }
}
