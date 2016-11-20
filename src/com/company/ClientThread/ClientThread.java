package com.company.ClientThread;


import com.company.Server.Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread implements Runnable  {
    private Socket connectionSocket = null;

    public ClientThread(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
    }

    public void run(){
        String clientSentence;
        String capitalizedSentence;
        BufferedReader fromClient =
                null;
        try {
            fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream toClient = null;
        try {
            toClient = new DataOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server server=new Server();
        try {
            server.doAnalize(fromClient,toClient,connectionSocket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
