package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainClient extends JFrame  {
   private  String host, command, username, password;
   private int port;
    private JFrame main;
    private JLabel label1, label2, label3, label4, label5;
    private JButton submit;
    private JPanel mainPanel;
    private TextField commandText;
    public MainClient(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        init();
    }
    public void init() {
        Font font = new Font("Garamond", Font.BOLD, 16);
        main = new JFrame();
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        main.setTitle("FTP Client");
        main.setSize(800, 500);

        label1 = new JLabel();
        label1.setText("Host: "+ host);
        label1.setBounds(37, 13, 200, 30);
        label1.setBackground(Color.GRAY);
        label1.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(label1);

        label2 = new JLabel();
        label2.setText("Port: "+port);
        label2.setBounds(400, 13, 200, 30);
        label2.setBackground(Color.GRAY);
        label2.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(label2);
        label3 = new JLabel("Username");
        label3.setBounds(37, 70, 50, 16);

        mainPanel.add(label3);

        label4 = new JLabel("Password");
        label4.setBounds(400, 70, 50, 16);

        mainPanel.add(label4);

        label5 = new JLabel();
        label5.setText("Command");
        label5.setBounds(200, 150, 80, 30);
        main.add(label5);

        commandText = new TextField();
        commandText.setBounds(300, 150, 200, 30);
        main.add(commandText);
        submit = new JButton("Submit");
        submit.setBounds(550, 150, 97, 25);
        main.add(submit);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread queryThread = new Thread() {
                    public void run()  {
                        executeCommand();

                    }
                };
                queryThread.start();
            }
        });
        main.add(mainPanel);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        main.setVisible(true);
    }

    private void executeCommand() {
        this.command = commandText.getText();
        String[] splitedCommand = this.command.split(" ");
        if(splitedCommand[0].toLowerCase().compareTo("ls") ==0) {
            ArrayList<String> listDir = new ArrayList<String>();
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                listDir = ftpClient.getList();
                String[] inputlist = new String[listDir.size()];
                inputlist = listDir.toArray(inputlist);
                JOptionPane.showMessageDialog(this, inputlist, "List of Files/Folders", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        if(splitedCommand[0].toLowerCase().compareTo("cd") == 0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                boolean status = ftpClient.setCd(splitedCommand[1]);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }
        if(splitedCommand[0].toLowerCase().compareTo("get") == 0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                ftpClient.getFiles(splitedCommand[1]);
            } catch (Exception e) {
                System.out.println("error"+e);
                e.printStackTrace();
                return;
            }
        }
    }


}
