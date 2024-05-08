package org.example;

import com.google.gson.Gson;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Client extends JFrame {
    // адрес сервера
    private static final String SERVER_HOST = ServerConfig.HOST;
    private static final int SERVER_PORT = ServerConfig.PORT;
    private Socket clientSocket;
    private BufferedReader in; // Поток чтения из сокета
    private BufferedWriter out; // Поток записи в сокет
    private final JTextArea jtfMessage; // Текстовое поле для ввода сообщения
    private final JTextField jtfName; // Текстовое поле для вврда логина
    private final JPanel messageLoginPanel = new JPanel();
    private final JScrollPane messageScrollPanel; // Скролл-панель для панели, на которой отображаются все сообщения
    private String clientName = "";// получаем имя клиента

    // Метод для отображения нового сообшения в основной панели
    private void createChatMessage(String login, String message, String type) {
        try {
            // Цвет рамки в зависимости от типа сообщения
            Color messageBackgroundColor = getBackgroundColor(login, type);

            JTextField tf = new JTextField(login);
            tf.setFont(new Font("Dialog", Font.ITALIC, 14));
            tf.setMargin(new Insets(5,5,5,5));
            tf.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
            tf.setEditable(false);
            tf.setBackground(messageBackgroundColor);
            tf.setForeground(new Color(50,50,50));

            JTextArea ta = new JTextArea();
            ta.append(message);
            ta.setFont(new Font("Dialog", Font.PLAIN, 14));
            ta.setMargin(new Insets(5,5,5,5));
            ta.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
            ta.setEditable(false);

            JPanel jpMessageLine = new JPanel();
            if (Objects.equals(login, clientName)) {
                jpMessageLine.setLayout(new FlowLayout(FlowLayout.LEFT));
            } else {
                jpMessageLine.setLayout(new FlowLayout(FlowLayout.RIGHT));
            }

            JPanel verticalPanelLoginMessage = new JPanel(); // Вертикальная панелька с логином и сообщением
            verticalPanelLoginMessage.setLayout(new BoxLayout(verticalPanelLoginMessage,BoxLayout.Y_AXIS));
            verticalPanelLoginMessage.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
            verticalPanelLoginMessage.setBackground(messageBackgroundColor);

            revalidate();

            jpMessageLine.add(verticalPanelLoginMessage);
            jpMessageLine.setBackground(Color.DARK_GRAY);

            verticalPanelLoginMessage.add(tf);
            verticalPanelLoginMessage.add(ta);
            messageLoginPanel.add(jpMessageLine);

            revalidate(); // отрисовываем новое сообщение

            JScrollBar vertical = messageScrollPanel.getVerticalScrollBar();
            vertical.setValue( vertical.getMaximum() );
            revalidate(); // перемещаем скролл в самый низ и отрисовываем

        } catch (RuntimeException ignored) {

        }
    }

    private Color getBackgroundColor(String login, String type) {
        Color messageBackgroundColor;
        if (Objects.equals(type, "END")) {
            messageBackgroundColor = new Color(0xFF9C9C);
        } else if (Objects.equals(type, "HELLO")){
            messageBackgroundColor = new Color(0xFFEA98);
        } else if (Objects.equals(login, clientName)) {
            messageBackgroundColor = new Color(0x5757FF);
        } else {
            messageBackgroundColor = new Color(0x83D99A);
        }
        return messageBackgroundColor;
    }

    // конструктор
    public Client(String login) {
        clientName = login;
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT); // подключаемся к серверу

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Поток чтения из сокета
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); // Поток записи в сокет

            // Отправляем приветственное сообщение, чтобы поток на сервере, отвечающий за наше подключение мог нас идентифицировать (Получить наш логин)
            Message pingMessage = new Message( clientName, "*подключился к чату", MessageType.HELLO);
            out.write(new Gson().toJson(pingMessage)+ "➽");
            out.flush();

        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

        // Задаём настройки элементов на форме
        setBounds(600, 300, 600, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        messageLoginPanel.setLayout(new BoxLayout(messageLoginPanel,BoxLayout.Y_AXIS));
        messageLoginPanel.setBackground(Color.RED);

        // Панель, на которой отображаются все сообщения
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(messageLoginPanel, BorderLayout.NORTH);
        messagePanel.setBackground(Color.DARK_GRAY);

        messageScrollPanel = new JScrollPane(messagePanel);
        messageScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(messageScrollPanel);

        // Панель, которая будет отображать количкство клиентов в чате
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));
        JLabel jlNumberOfClientsLabel = new JLabel("Количество клиентов в чате: ");
        JLabel jlNumberOfClientsNumber = new JLabel("0");
        topPanel.add(jlNumberOfClientsLabel);
        topPanel.add(jlNumberOfClientsNumber);
        add(topPanel, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Отправить");

        jtfMessage = new JTextArea("Введите ваше сообщение: ");
        jtfMessage.setLineWrap(true);
        jtfMessage.setEditable(true);
        jtfMessage.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(jtfMessage);
        messageScroll.setPreferredSize(new Dimension(100, 50));
        messageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messageScroll.setAutoscrolls(true);

        bottomPanel.add(messageScroll, BorderLayout.CENTER);
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfName = new JTextField(login);
        jtfName.setHorizontalAlignment(JLabel.CENTER);
        jtfName.setPreferredSize(new Dimension(100, 50));
        jtfName.setEditable(false);
        bottomPanel.add(jtfName, BorderLayout.WEST);

        // обработчик события нажатия кнопки отправки сообщения
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    try {
                        sendMsg();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    jtfMessage.grabFocus(); // фокус на текстовое поле с сообщением
                }
            }
        });
        // при фокусе поле сообщения очищается
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        //
        jtfMessage.addKeyListener(new KeyListener() {
            boolean alt = false;
            @Override
            public void keyTyped(KeyEvent e) {
                if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
                    alt = true;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
                    alt = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (alt && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jbSendMessage.doClick();
                    alt = false;
                }
            }
        });

        // Сразу как наш фрейм откроктся, зафокусим поле ввода сообщения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                jtfMessage.requestFocus();
            }
        });

        // в отдельном потоке начинаем работу с сервером
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // бесконечный цикл
                    while (true) {
                        StringBuilder sb = new StringBuilder();
                        boolean sendToChat = true;

                        int c = in.read();

                        while(c!=-1){
                            if (c == 10173) {
                                break;
                            }

                            char ch = (char)c;

                            sb.append(ch);
                            c = in.read();
                        }

                        String word = sb.toString();
                        JSONObject jsonObject = new JSONObject(word);

                        StringBuilder sbCorrect = new StringBuilder();
                        String getMessage = jsonObject.getString("message");

                        if (Objects.equals(jsonObject.getString("type"), "PING")) {
                            sendToChat = false;
                            Message pingMessage = new Message( clientName, "pong", MessageType.PING);
                            out.write(new Gson().toJson(pingMessage)+ "➽");
                            out.flush();
                        }

                        char[] charArray = getMessage.toCharArray();
                        int counter = 0;
                        int lastSpace = 0;
                        int lastSpaceSymbols = 0;

                        for (int i = 0; i < charArray.length; i++) {
                            char symbol = charArray[i];
                            if (symbol == ' ' || symbol == '\t' || symbol == '\r' || symbol == '\n') {
                                lastSpace = i;
                                lastSpaceSymbols = 0;
                            }

                            if (counter > 50) {
                                sbCorrect.delete(sbCorrect.length() + 1 - lastSpaceSymbols, sb.length());
                                sbCorrect.append('\n'); // добавляем перенос строки
                                i = lastSpace; // переносим каретку к последнему пробелу
                                counter = 0;
                                lastSpace = 0;
                            } else {
                                sbCorrect.append(symbol);
                            }

                            counter++;
                            lastSpaceSymbols++;
                        }

                        if (sendToChat) {
                            if (jsonObject.has("clientsCount") && !jsonObject.isNull("clientsCount")) {
                                jlNumberOfClientsNumber.setText(String.valueOf(jsonObject.getInt("clientsCount")));
                            }
                            createChatMessage(jsonObject.getString("login"), sbCorrect.toString(), jsonObject.getString("type"));
                        }
                    }
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
            }
        }).start();
        // добавляем обработчик события закрытия окна клиентского приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    Message pingMessage = new Message(clientName, "*Вышел из чата.", MessageType.END);
                    out.write(new Gson().toJson(pingMessage)+ "➽");
                    out.flush();
                    clientSocket.close();
                } catch (IOException exc) {
                    System.out.print("close error");
                }
            }
        });
        // отображаем форму
        setVisible(true);
    }

    // отправка сообщения
    public void sendMsg() throws IOException {
        // формируем сообщение для отправки на сервер
        Message message = new Message(jtfName.getText(), jtfMessage.getText(), MessageType.MESSAGE);

        out.write(new Gson().toJson(message)+ "➽");
        out.flush();
        jtfMessage.setText("");
    }
}
