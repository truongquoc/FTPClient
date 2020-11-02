package com;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class FtpClient {
    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;
    public static FileOutputStream fos = null;
    public static BufferedOutputStream bos = null;
    private String hostname, username, password;
    private int port;
    private static FtpClient Instance = null;
    public static String msg ="";
    public final static int FILE_SIZE = 6022386;

private FtpClient(String hostName, int port, String username, String password) throws Exception {
  this.hostname = hostName;
  this.port = port;
  this.username = username;
  this.password = password;
    System.out.println("host: "+this.hostname+" Port: "+this.port);
  Socket socket = new Socket(hostName, this.port);

  oos = new ObjectOutputStream(socket.getOutputStream());
  ois = new ObjectInputStream(socket.getInputStream());
  oos.writeObject(username);
  oos.writeObject(password);
  msg = (String) ois.readObject();
    System.out.println("msg" +msg);
}
    public static FtpClient getInstance(String hostName, int port, String username, String password) throws Exception {
        if(Instance == null) {
            Instance = new FtpClient(hostName, port, username, password);
        }
        return Instance;
    }

public static ArrayList<String> getList() throws IOException {
    try {
        ArrayList<String> listDir = new ArrayList<String>();
        oos.writeObject("LS");
        int length = ois.readInt();
        for(int index =0; index < length; index ++) {
            String result = (String) ois.readObject();
            listDir.add(result);
        }
        return listDir;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

public static boolean setCd(String path) {
    try {
        oos.writeObject("CD");
        oos.writeObject(path);
        String status = (String) ois.readObject();
        if(status.compareTo("true") == 0) return true;
        else  return false;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public void getFiles(String filename) {
    try {
        Socket dataSocket = new Socket(this.hostname, this.port -1 );
        oos.writeObject("GET");
        oos.writeObject(filename);
        File f = new File(filename);
        ObjectInputStream dis = new ObjectInputStream(dataSocket.getInputStream());
        int length = dis.readInt();
        System.out.println("length"+ length);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buffer = new byte[100];
        int BytesRead;
        do {
            BytesRead = dis.readInt();
            Object obj = dis.readObject();
            buffer = (byte[]) obj;
            fos.write(buffer, 0, BytesRead);
        } while (BytesRead == 100);
        fos.close();
        dataSocket.close();

    } catch (Exception e) {
        System.out.println("err"+ e);
        e.printStackTrace();
    }
}
}
