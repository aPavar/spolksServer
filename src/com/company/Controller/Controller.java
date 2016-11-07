package com.company.Controller;

import com.company.CharacterTransferData.CharacterTransferData;
import com.company.File.File;
import com.company.Header.Header;
import com.company.KitOfHeaders.KitOfHeaders;
import com.company.NameCommand.NameCommand;
import com.company.Server.Server;
import com.company.SpecialData.SpecialData;
import com.company.StructureOfReturnValue.StructureOfReturnValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;


public class Controller {
    public static int sizeOfPackage= 1024;
    public static int port=6000;
    public static int sizeOfPackageUdp = 16384;

public static void newConnection(Socket connectionSocket) throws IOException, ClassNotFoundException {
    String clientSentence;
    String capitalizedSentence;
    BufferedReader fromClient =
            new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    DataOutputStream toClient = new DataOutputStream(connectionSocket.getOutputStream());
    Server server=new Server();
    server.doAnalize(fromClient,toClient,connectionSocket);

}

    public byte[] charArrayToByteArray(char[] beginArray){
        String string=new String(beginArray);
        return string.getBytes();
    }

    public static void move() throws IOException, ClassNotFoundException {

        System.out.println("Server start");
    ServerSocket welcomeSocket = new ServerSocket(port);


    while (true) {
        Socket connectionSocket = welcomeSocket.accept();
        connectionSocket.setOOBInline(true);
       // connectionSocket.setKeepAlive(true);
      //  connectionSocket.setTrafficClass(10);
        connectionSocket.setSoTimeout(22000);
        connectionSocket.setReceiveBufferSize(1000*Controller.sizeOfPackage);
        connectionSocket.setSendBufferSize(2*Controller.sizeOfPackage);
        System.out.println("New Connection");
        newConnection(connectionSocket);

        }
    }








}
