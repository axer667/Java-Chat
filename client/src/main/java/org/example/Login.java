package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {
    String login;
    public Login() {
        setBounds(600, 300, 200, 100);
        setTitle("Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JTextField jtfLogin = new JTextField("Введите ваше имя: ");
        jtfLogin.setPreferredSize(new Dimension(100, 50));
        JButton jbSendLogin = new JButton("Отправить");

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
        centerPanel.add(jtfLogin);
        centerPanel.add(jbSendLogin);
        add(centerPanel, BorderLayout.CENTER);
        jbSendLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfLogin.getText().trim().isEmpty()) {
                    login = jtfLogin.getText();
                    setVisible(false);
                    new Client(login);
                }
            }
        });
        jtfLogin.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfLogin.setText("");
            }
        });
        jtfLogin.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jbSendLogin.doClick();
                }
            }
        });
        setVisible(true);
    }
}
