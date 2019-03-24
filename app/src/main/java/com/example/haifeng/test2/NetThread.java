package com.example.haifeng.test2;

import android.widget.TextView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NetThread implements Runnable {

    ServerSocket serverSocket;
    int port;
    boolean isConnectionEstablished = false;
    OutputStream outputStream;
    PrintWriter printWriter;
    Socket socket;
    TextView statu;
    Data data;
    NetThread(int port, Data data, TextView t){
        this.port = port;
        this.data = data;
        this.statu = t;
    }

    @Override

    public void run() {

//run permanently, handling network communication
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("thread waiting");
            statu.post(new Runnable() {
                @Override
                public void run() {
                    statu.setText("thread waiting");
                }
            });
            socket = serverSocket.accept();
            statu.post(new Runnable() {
                @Override
                public void run() {
                    statu.setText("connection established");
                }
            });
            System.out.println("Client: " + socket.getInetAddress().getHostAddress() + " connected");
            isConnectionEstablished = true;
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            printWriter.println("Package delivered!");
            printWriter.flush();
            while(true) {
                while (data.sendFlag) {
                    statu.post(new Runnable() {
                        @Override
                        public void run() {
                            statu.setText("sending");
                        }
                    });
                    while (data.sendable) {
                        printWriter.println(data.v0);
                        printWriter.println(data.v1);
                        printWriter.println(data.v2);
                        printWriter.println(data.v3);
                        printWriter.println(data.v4);
                        printWriter.println(data.v5);
                        printWriter.flush();
                        data.sendable = false;
                        Thread.sleep(500);
                    }
                    Thread.sleep(500);
                }
                Thread.sleep(500);
            }
 //           socket.shutdownInput();
 //           outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean shutdownConnection() {
        try {
            socket.shutdownInput();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
