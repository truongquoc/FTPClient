package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientUI extends JFrame implements ActionListener {
    public static JFrame mainFrame;
    private JLabel label1, label2, label3, label4;
    private JButton submit;
    private TextField host, username, port, password;
    private JPanel mainPanel;

    public static void main(String[] args) throws  Exception{
        new ClientUI("FTP Client");
    }

    public ClientUI(String title) throws IOException, ClassNotFoundException {
        super(title);
        GUI();
    }
    public void GUI() {
        mainFrame = new JFrame();
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        mainFrame.setTitle("FTP Client");
        mainFrame.setSize(800, 400);

        label1 = new JLabel("Host");
        label1.setBounds(37, 13, 50, 16);
        host = new TextField();
        host.setBounds(150, 13, 169, 30);
        mainPanel.add(label1);
        mainPanel.add(host);

        label2 = new JLabel("Port");
        label2.setBounds(400, 13, 50, 16);
        port = new TextField();
        port.setBounds(513, 13, 169, 30);

        mainPanel.add(label2);
        mainPanel.add(port);

        label3 = new JLabel("Username");
        label3.setBounds(37, 70, 80, 16);
        username = new TextField();
        username.setBounds(150, 70, 169, 30);
        mainPanel.add(label3);
        mainPanel.add(username);

        label4 = new JLabel("Password");
        label4.setBounds(400, 70, 70, 16);
        password = new TextField();
        password.setBounds(513, 70, 169, 30);
        mainPanel.add(label4);
        mainPanel.add(password);

        submit = new JButton("Connect to Server");
        submit.setBounds(350, 150, 197, 25);
        submit.addActionListener(this);
        mainFrame.add(submit);
        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getSource() == submit) {
                String hostname = (String) this.host.getText();
                String username = (String) this.username.getText();

                String password = (String) this.password.getText();
                if(hostname.isEmpty() || username.isEmpty() || password.isEmpty() || this.port.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill the required fields", "Error",  JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int port = Integer.parseInt(this.port.getText());
                FtpClient ftpClient = FtpClient.getInstance(hostname, port, username, password);
                if(ftpClient.msg.compareTo("Success") == 0) {
                    System.out.println("working");
                    mainFrame.setVisible(false);
                    MainClient mainClient = new MainClient(hostname, port, username, password);
                } else {
                    System.out.println("message"+ftpClient.msg);
                    JOptionPane.showMessageDialog(this, "Username or Password are incorrect", "Error",  JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
