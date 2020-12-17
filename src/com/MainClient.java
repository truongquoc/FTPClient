package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainClient extends JFrame  {
   private  String host, command, username, password;
   private int port;
    public static JFrame main, progressPan;
    private JLabel label1, label2, label3, label4, label5, progressText, currentDir, currentDirServer;
    private JButton submit, getButton, putButton, helpButton, disConnectButton, changeDirButton, rmDirButton, deleteFileButton;
    public static JPanel mainPanel;
    private TextField commandText;
    private JTextArea content;
    private JScrollPane sp;
    public static  JProgressBar progressBar;
    public static Dialog dialog;
    public static JFileChooser fileChooser;
    public MainClient(String host, int port, String username, String password) throws Exception{
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        init();
    }
    public void init() throws  Exception {
        Font font = new Font("Garamond", Font.BOLD, 16);
        main = new JFrame();
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        main.setTitle("FTP Client");
        main.setSize(800, 800);

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
        label5.setBounds(50, 670, 80, 30);
        main.add(label5);

        commandText = new TextField();
        commandText.setBounds(150, 670, 300, 30);
        main.add(commandText);

        content = new JTextArea();
        content.getAccessibleContext();
        sp = new JScrollPane(content);
        sp.setBounds(50, 150, 600, 500);
        sp.getAccessibleContext();


        main.add(sp);


        progressText = new JLabel("Progress");
        progressText.setBounds(50, 130, 100, 20);
        main.add(progressText);
        progressBar = new JProgressBar();
        progressBar.setBounds(140, 130, 200, 20);
        progressBar.setBorderPainted(true);
        main.add(progressBar);

        FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
        String dir = ftpClient.getCurrentWorkingDir();
        System.out.println("dir"+dir);
        currentDirServer = new JLabel("Location: " +dir);
        currentDirServer.setBounds(400, 130, 300, 20);
        main.add(currentDirServer);
        submit = new JButton("Execute Command");
        submit.setBounds(450, 670, 197, 30);

        changeDirButton = new JButton("Change Dir");
        changeDirButton.setBounds(50, 720, 150, 30);
        changeDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new java.io.File("."));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if(fileChooser.showOpenDialog(MainClient.this) == JFileChooser.APPROVE_OPTION) {
                    currentDir.setText( fileChooser.getSelectedFile().toString());
                } else {
                    System.out.println("No selection");
                }
            }
        });
        main.add(changeDirButton);
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("current"+s);
        currentDir = new JLabel(s);
        currentDir.setBounds(220, 720, 600, 20);
        currentDir.setBackground(Color.GRAY);
        main.add(currentDir);
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

        getButton = new JButton("Get File");
        getButton.setBounds(650, 150, 120, 25);
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                    ArrayList<String> list = new ArrayList<String>();
                    list = ftpClient.getFilesOnly();
                    String selection = (String) JOptionPane.showInputDialog(MainClient.this, "Choose a File to delete", "Input", JOptionPane.QUESTION_MESSAGE,
                            null, list.toArray(), "Titan");
                    if(selection == null) {
                        return;
                    }
                    ftpClient.getFiles(selection, currentDir.getText());
                    JOptionPane.showMessageDialog(main, "Completed Downloading!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        });
        main.add(getButton);

        putButton = new JButton("Put File");
        putButton.setBounds(650, 200, 120, 25);
        putButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                    fileChooser = new JFileChooser();
                    ftpClient.sendFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        });
        main.add(putButton);
        rmDirButton = new JButton("Remove Dir");
        rmDirButton.setBounds(650, 250, 120, 25);
        rmDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                    ArrayList<String> list = new ArrayList<String>();
                    list = ftpClient.getDir();
                    String [] inputList = new String[list.size()];
                    inputList = list.toArray(inputList);
                    String selection = (String) JOptionPane.showInputDialog(MainClient.this, "Choose a Folder to delete", "Input", JOptionPane.QUESTION_MESSAGE,
                            null, inputList, "Titan");
                    System.out.println("selection"+ selection);
                    if(selection == null) {
                        return;
                    }
                    boolean status = ftpClient.rmDir(selection);
                    if(!status) {
                        JOptionPane.showMessageDialog(MainClient.this, "fail to remove: No such a file or directory", "Error",  JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        });
        main.add(rmDirButton);

        deleteFileButton = new JButton("Delete File");
        deleteFileButton.setBounds(650, 300, 120, 25);
        deleteFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                    ArrayList<String> list = new ArrayList<String>();
                    list = ftpClient.getFilesOnly();
                    String selection = (String) JOptionPane.showInputDialog(MainClient.this, "Choose a File to delete", "Input", JOptionPane.QUESTION_MESSAGE,
                            null, list.toArray(), "Titan");
                    if(selection == null) {
                        return;
                    }
                    boolean status = ftpClient.delete(selection);
                    if(!status) {
                        JOptionPane.showMessageDialog(MainClient.this, "fail to remove: No such a file or directory", "Error",  JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        });
        main.add(deleteFileButton);

        disConnectButton = new JButton("DisConnect");
        disConnectButton.setBounds(650, 350, 120, 25);
        disConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              try {
                  FtpClient ftpClient = FtpClient.getInstance(host, port, username, password);
                  ftpClient.DisConnectServer();
                  MainClient.main.setVisible(false);
                  FtpClient.Instance = null;
                  ClientUI.mainFrame.setVisible(true);
              } catch (Exception err) {
                  err.printStackTrace();
              }

            }
        });
        main.add(disConnectButton);



        main.add(mainPanel);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        main.setVisible(true);
    }

    private void executeCommand() {
        this.command = commandText.getText();
        String[] splitedCommand = this.command.split(" ");
        content.append(command+"\n");

        if(splitedCommand[0].toLowerCase().compareTo("ls") ==0) {
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
                currentDirServer.setText("Location: "+res);
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
                ftpClient.getFiles(selection, currentDir.getText());
                JOptionPane.showMessageDialog(main, "Completed Downloading!");
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
