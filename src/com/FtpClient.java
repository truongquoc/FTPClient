package com;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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

public static String getCurrentWorkingDir() {
    try {
        oos.writeObject("PWD");
        String dir = (String) ois.readObject();
        return dir;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

public boolean newDir(String dirName) {
    try {
        oos.writeObject("MKDIR");
        oos.writeObject(dirName);
        String status = (String) ois.readObject();
        System.out.println("staus"+ status);
        if(status.compareTo("true") == 0) {
            return true;
        }
        return false;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public static String setCd(String path) {
    try {
        oos.writeObject("CD");
        oos.writeObject(path);
        String response = (String) ois.readObject();
        return response;

    } catch (Exception e) {
        e.printStackTrace();
        return e.getMessage();
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
        double updateLength =0, progValue = 0;
        do {
            BytesRead = dis.readInt();
            updateLength+= BytesRead;
            System.out.println("update"+ updateLength+" length "+length);
            progValue = ((updateLength / ((double)length)) * 100);
            Object obj = dis.readObject();
            buffer = (byte[]) obj;
            fos.write(buffer, 0, BytesRead);
            MainClient.progressBar.setValue((int)progValue);
            System.out.println("percen"+updateLength/length);
            MainClient.progressBar.update(MainClient.progressBar.getGraphics());
        } while (BytesRead == 100);
        fos.close();
        dataSocket.close();

    } catch (Exception e) {
        System.out.println("err"+ e);
        e.printStackTrace();
    }
}

public void sendFile() {
    try {
        Socket dataSocket = new Socket(hostname, port-1);
        oos.writeObject("PUT");
        ObjectOutputStream dos = new ObjectOutputStream(dataSocket.getOutputStream());
        int i = MainClient.fileChooser.showOpenDialog(MainClient.main);
        if(i == JFileChooser.APPROVE_OPTION) {
            File f = MainClient.fileChooser.getSelectedFile();
            String filename = f.getName();
            oos.writeObject(filename);
            FileInputStream fis = new FileInputStream(f);
            double length = f.length();
            double updateLength=0;
            byte[] buffer = new byte[1024];
            Integer bytesRead=0;
            while((bytesRead = fis.read(buffer)) > 0) {
                updateLength+=bytesRead;
                double progress = (updateLength / length)*100;
                MainClient.progressBar.setValue((int) progress);
                dos.writeInt(bytesRead);
                dos.writeObject(Arrays.copyOf(buffer, buffer.length));
            }
            dataSocket.close();
            fis.close();
        }
    } catch (Exception e) {
        return;
    }
}

public ArrayList<String> getDir() {
    try {
        ArrayList<String> list = new ArrayList<String>();
        oos.writeObject("GET_DIR");
        int length = ois.readInt();
        for(int i=0; i<length; i++) {
            String filename = (String) ois.readObject();
            list.add(filename);
        }
        return list;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

public ArrayList<String> getFilesOnly() {
    try {
        ArrayList<String> list = new ArrayList<String>();
        oos.writeObject("GET_FILES");
        int length = ois.readInt();
        for(int i=0; i<length; i++) {
            String filename = (String) ois.readObject();
            list.add(filename);
        }
        return list;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
public boolean rmDir(String dirName) {
        try {
            oos.writeObject("RMDIR");
            oos.writeObject(dirName);
            String status = (String) ois.readObject();
            System.out.println("status"+status);
            if(status.compareTo("true") == 0) return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
}
public boolean delete(String filename) {
    try {
        oos.writeObject("DELETE");
        oos.writeObject(filename);
        String status = (String) ois.readObject();
        if(status.compareTo("true") == 0) return true;
        return false;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}
