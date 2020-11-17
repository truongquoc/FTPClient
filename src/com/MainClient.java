package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.util.ArrayList;

public class MainClient extends JFrame  {
   private  String host, command, username, password;
   private int port;
    public static JFrame main, progressPan;
    private JLabel label1, label2, label3, label4, label5;
    private JButton submit;
    public static JPanel mainPanel;
    private TextField commandText;
    private JTextArea content;
    private JScrollPane sp;
    public static  JProgressBar progressBar;
    public static Dialog dialog;
    public static JFileChooser fileChooser;
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
        label3 = new JLabel("Username: "+username);
        label3.setBounds(37, 70, 200, 30);
        label3.setBackground(Color.GRAY);
        label3.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(label3);

        label4 = new JLabel("Password: "+password);
        label4.setBounds(400, 70, 200, 30);
        label4.setBackground(Color.GRAY);
        label4.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(label4);

        label5 = new JLabel();
        label5.setText("Command");
        label5.setBounds(200, 350, 80, 30);
        main.add(label5);

        commandText = new TextField();
        commandText.setBounds(300, 350, 200, 30);
        main.add(commandText);

        content = new JTextArea();
        content.getAccessibleContext();
        sp = new JScrollPane(content);
        sp.setBounds(50, 150, 500, 200);
        sp.getAccessibleContext();


        main.add(sp);


        progressBar = new JProgressBar();
        progressBar.setBounds(50, 130, 160, 20);
        progressBar.setBorderPainted(true);
        main.add(progressBar);


        submit = new JButton("Submit");
        submit.setBounds(550, 350, 97, 25);
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
        content.append(command+"\n");
        if(splitedCommand[0].toLowerCase().compareTo("ls") ==0) {
//            try {
//                System.out.println("remote port"+ FtpClient.socket.getInetAddress().isReachable());
//            } catch (Exception e) {
//                System.out.println("error"+e.getMessage());
//            }
//            if(FtpClient.socket.g)
            ArrayList<String> listDir = new ArrayList<String>();
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                listDir = ftpClient.getList();
                String[] inputlist = new String[listDir.size()];
                inputlist = listDir.toArray(inputlist);
                for(int index=0; index<inputlist.length; index++) {
                    content.append(inputlist[index]+"\n");
                }
                JScrollBar vertical = sp.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        else if(splitedCommand[0].toLowerCase().compareTo("cd") == 0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                String res = ftpClient.setCd(splitedCommand[1]);
                content.append(res+"\n");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }
        else if(splitedCommand[0].toLowerCase().compareTo("get") == 0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                ArrayList<String> list = new ArrayList<String>();
                list = ftpClient.getFilesOnly();
                String selection = (String) JOptionPane.showInputDialog(this, "Choose a File to delete", "Input", JOptionPane.QUESTION_MESSAGE,
                        null, list.toArray(), "Titan");
                if(selection == null) {
                    return;
                }
                ftpClient.getFiles(selection);

            } catch (Exception e) {
                System.out.println("error"+e);
                e.printStackTrace();
                return;
            }
        }
        else if(splitedCommand[0].toLowerCase().compareTo("pwd") == 0) {
            if(splitedCommand.length >1) {
                JOptionPane.showMessageDialog(main, "Invalid command");
                return;
            }
           try {
               FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                String dir = ftpClient.getCurrentWorkingDir();
                content.append(dir+"\n");
           } catch (Exception e) {
               e.printStackTrace();
               return;
           }

        }

        else if(splitedCommand[0].toLowerCase().compareTo("mkdir") == 0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);

                for(int i=1; i<splitedCommand.length; i++) {
                   boolean status = ftpClient.newDir(splitedCommand[i]);
                   if(!status) {
                       content.append("Cannot create Directory '"+splitedCommand[i]+"': File exists "+"\n");
                   }
                }
            } catch (Exception e) {
                System.out.println("error"+e);
                e.printStackTrace();
                return;
            }
        }

        else if(splitedCommand[0].toLowerCase().compareTo("put") == 0) {
            try{
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                 fileChooser = new JFileChooser();
                ftpClient.sendFile();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("err"+e);
                return;
            }
        }

        else if (splitedCommand[0].toLowerCase().compareTo("rmdir") == 0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                ArrayList<String> list = new ArrayList<String>();
                list = ftpClient.getDir();
                String [] inputList = new String[list.size()];
                inputList = list.toArray(inputList);
                String selection = (String) JOptionPane.showInputDialog(this, "Choose a Folder to delete", "Input", JOptionPane.QUESTION_MESSAGE,
                        null, inputList, "Titan");
                System.out.println("selection"+ selection);
                if(selection == null) {
                    return;
                }
               boolean status = ftpClient.rmDir(selection);
                if(!status) {
                    JOptionPane.showMessageDialog(this, "fail to remove: No such a file or directory", "Error",  JOptionPane.ERROR_MESSAGE);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        else if(splitedCommand[0].toLowerCase().compareTo("delete") ==0) {
            try {
                FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                ArrayList<String> list = new ArrayList<String>();
                list = ftpClient.getFilesOnly();
                String selection = (String) JOptionPane.showInputDialog(this, "Choose a File to delete", "Input", JOptionPane.QUESTION_MESSAGE,
                        null, list.toArray(), "Titan");
                if(selection == null) {
                    return;
                }
                boolean status = ftpClient.delete(selection);
                if(!status) {
                    JOptionPane.showMessageDialog(this, "fail to remove: No such a file or directory", "Error",  JOptionPane.ERROR_MESSAGE);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }


}
