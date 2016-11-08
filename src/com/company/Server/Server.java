package com.company.Server;

import com.company.CharacterTransferData.CharacterTransferData;
import com.company.Controller.Controller;
import com.company.File.File;
import com.company.Header.Header;
import com.company.HeaderUdp.HeaderUdp;
import com.company.KitOfHeaders.KitOfHeaders;
import com.company.NameCommand.NameCommand;
import com.company.Separator.Separator;
import com.company.SpecialData.SpecialData;
import com.company.StructureOfReturnValue.StructureOfReturnValue;
import com.company.UndefinedProtocol.UndefinedProtocol;
import com.sun.deploy.Environment;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class Server {

    public static String folderToLoad=java.io.File.pathSeparator+"destinationServer";

    public void doAnalize(BufferedReader fromClient, DataOutputStream toClient,Socket socket)
            throws IOException, ClassNotFoundException {
        while (true) {
            try {
            byte[] arrayFromClient = new byte[Controller.sizeOfPackage];
            readPackForFile(socket,arrayFromClient);


            String string=new String(arrayFromClient,Charset.defaultCharset());

            //  KitOfHeaders kitOfHeaders=new KitOfHeaders();
            //  analizePacketFromServer(arrayFromClient,kitOfHeaders);
            Separator separator = new Separator();
            separator.parce(arrayFromClient);


              if(separator.isProtocol()) {
                  if (separator.getHeader().getNameCommand() == NameCommand.loadFile) {

                      loadFile(separator.getMessage(), separator, fromClient,toClient,socket);
                  } else {
                      if (separator.getHeader().getNameCommand() == NameCommand.downloadFile) {
                          downloadFile(separator.getMessage(),separator,fromClient,toClient,socket,
                                  separator.getHeader().getNumberOfPacket());
                      } else {
                          if (separator.getHeader().getNameCommand() == NameCommand.echo) {
                              echo(separator.getMessage(), toClient, fromClient);
                          } else {
                              if (separator.getHeader().getNameCommand() == NameCommand.getTime) {
                                  getTime(fromClient, toClient);
                              }else{
                                  if (separator.getHeader().getNameCommand()==NameCommand.shutDown)
                                      System.exit(0);
                                  else{
                                      if(separator.getHeader().getNameCommand()==NameCommand.loadFileUdp)
                                          loadFileUdp(separator, socket);
                                      else{
                                          if(separator.getHeader().getNameCommand()==NameCommand.downloadFileUdp)
                                              downloadFileUdp();
                                      }
                                  }
                              }
                          }
                      }
                  }
              }else{
                if(separator.isTelnet()) {
                    if (separator.getTelnetComand().equals("echo"))
                        echoTelnet(separator.getMessageTelnet(), toClient);
                    else {
                        if (separator.getTelnetComand().equals("time"))
                            timeTelnet(toClient);
                        }
                }else{
                    if(separator.isUnknown())
                        if(separator.getMessage()==null && separator.getHeader()==null)
                            throw new UndefinedProtocol();
                }
              }

            }catch (SocketException|SocketTimeoutException e){
                System.out.println("Client is damaged");
                break;
            }catch (UndefinedProtocol undefinedProtocol) {
                System.out.println("Undefined protocol");
                break;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            catch(IOException e){
                System.out.println("IOException");
                break;
            }

        }
    }

    public void echoTelnet(String stringRet, DataOutputStream toClient) throws IOException {
      //  sendPackage(stringRet.getBytes(),toClient);
        toClient.writeChars(stringRet);
    }

    public void timeTelnet(DataOutputStream toClient) throws IOException {
        Date date=new Date(System.currentTimeMillis());
      //  sendPackage(date.toString().getBytes(),toClient);
        toClient.writeChars(date.toString());

    }

    public void loadFile(byte[] nameOfFileInByte,  Separator separator, BufferedReader fromClient,DataOutputStream toClient,
                         Socket socket) throws IOException, ClassNotFoundException, InterruptedException {
        //socket.shutdownOutput();

        String nameOfFile=new String(nameOfFileInByte,Charset.defaultCharset());
        byte[] arrayFromClient=null;
        int a=separator.getHeader().getNumberOfPacket();

        File file=new File();
        int mul=50;
        int j=0;
        byte[] cache=new byte[mul*Controller.sizeOfPackage];
        int sm=0;
        long pos=a*(Controller.sizeOfPackage-Header.sizeOfHeader);
        int size=0;
       // socket.getOutputStream().write("lol".getBytes());
        for(int i=0;i<separator.getHeader().getCountOfPacket()-a;i++){
            arrayFromClient=new byte[Controller.sizeOfPackage];


            readPackFull(socket,arrayFromClient);
           // socket.getOutputStream().write("lol".getBytes());

            //


            separator.parce(arrayFromClient);
            System.out.println(separator.getHeader().getNumberOfPacket());
            if(separator.getHeader().getNameCommand()!=NameCommand.loadFile)
                throw new SocketException();
            if(separator.getHeader().getNumberOfPacket()<i) {
                i = separator.getHeader().getNumberOfPacket();
                file.writeInfoInFileSpec(nameOfFile,cache,pos,size);
                pos=i*(Controller.sizeOfPackage-Header.sizeOfHeader);
                sm=0;
                j=0;
                size=0;
            }

            System.arraycopy(separator.getMessage(),0,cache,sm,separator.getHeader().getSizeOfMessage());
            sm+=separator.getHeader().getSizeOfMessage();

            size+=separator.getMessage().length;
            j++;
            if(j%mul==0&&i!=0)
            {
                sm=0;
                j=0;

            //    jjjj=1;
            }



            if(j==0&&i!=0){


                file.writeInfoInFileSpec(nameOfFile,cache,pos,size);
                pos+=size;
                size=0;

            }
            if(separator.getHeader().getSizeOfMessage()!=Controller.sizeOfPackage-Header.sizeOfHeader)
                file.writeInfoInFileSpec(nameOfFile,cache,pos,size);

        }



    }

    public void readPackForFile(Socket s,byte[] array) throws IOException {
        DataInputStream dataInputStream=new DataInputStream(s.getInputStream());

        dataInputStream.read(array);

    }


    public void readPackFull(Socket s, byte[] array) throws IOException {
        DataInputStream dataInputStream=new DataInputStream(s.getInputStream());

        dataInputStream.readFully(array);
    }


    public void downloadFile(byte[] nameOfFileInByte,  Separator separator, BufferedReader fromClient,DataOutputStream toClient,
                             Socket socket,int number) throws IOException {
        String nameOfFile=new String(nameOfFileInByte,Charset.defaultCharset());

        File file=new File(nameOfFile);
        int length=Controller.sizeOfPackage-Header.sizeOfHeader;
        byte[] arrayToServer=new byte[Controller.sizeOfPackage-Header.sizeOfHeader];
        long position=number*(Controller.sizeOfPackage-Header.sizeOfHeader);


        createListPackets(nameOfFile.getBytes(),Controller.sizeOfPackage,CharacterTransferData.setConnection,NameCommand.downloadFile,
                countOfPackets(file.getSizeOfFile()),0);

        sendPackage(listOfPackets.get(0),toClient);
        listOfPackets.clear();

    //   int aaa= socket.getInputStream().read(arrayFromServer);
        StructureOfReturnValue structureOfReturnValue=new StructureOfReturnValue (false,0);
        int count=countOfPackets(file.getSizeOfFile());
        int jo=number;
        while(!structureOfReturnValue.isSignalOfEndFile()) {
            structureOfReturnValue= file.readInfoFromFile(nameOfFile, length, position,arrayToServer);
            createListPackets(arrayToServer, Controller.sizeOfPackage,CharacterTransferData.transferData,NameCommand.downloadFile,count,jo,
                    structureOfReturnValue.getLengthOfReadCharacter());
            for (int i = 0; i < listOfPackets.size(); i++) {
                //    readPackage(arrayFromServer,fromServer);

                sendPackage(listOfPackets.get(i), toClient);
             //   aaa=socket.getInputStream().read(arrayFromServer);

            }
            System.out.println(jo);
            jo++;
            listOfPackets.clear();
            position+=length;

        }


    }
    public void createListPackets(byte[] data,int sizeOfPacket, CharacterTransferData characterTransferData,NameCommand nameCommand,
                                  int countOfPacket,int numberOfPacket ) throws IOException {


        listOfPackets=new ArrayList<>();
        Header header=new Header();
        int i=0;
        header.setInformation(characterTransferData, nameCommand,i,sizeOfPacket,0);

        while(true) {
            byte[] newData=new byte[sizeOfPacket];
            header.setNumberOfPacket(i);

            if((data.length-i*(sizeOfPacket- header.getSizeOfHeader()))>sizeOfPacket-header.getSizeOfHeader()) {
                //  System.out.println(header.getSizeOfHeader());
                header.setSizeOfMessage(sizeOfPacket-header.getSizeOfHeader());
                header.setCountOfPacket(countOfPacket);
                header.setNumberOfPacket(numberOfPacket+i);
                System.arraycopy(Header.headerToArrayOfBytes(header), 0, newData, 0, header.getSizeOfHeader());
                System.arraycopy(data, i * (sizeOfPacket-header.getSizeOfHeader()), newData,header.getSizeOfHeader() ,
                        sizeOfPacket-header.getSizeOfHeader());
                header.setSizeOfMessage(sizeOfPacket-header.getSizeOfHeader());
                listOfPackets.add(newData);
                numberOfPacket++;

            }else  {
                header.setSizeOfMessage(data.length-i*(sizeOfPacket-header.getSizeOfHeader()));
                header.setNumberOfPacket(numberOfPacket+i);
                header.setCountOfPacket(countOfPacket);
                System.arraycopy(Header.headerToArrayOfBytes(header),0,newData,0,header.getSizeOfHeader());

                System.arraycopy(data, i * (sizeOfPacket-header.getSizeOfHeader()),newData,header.getSizeOfHeader() ,
                        data.length-i*(sizeOfPacket-header.getSizeOfHeader()) );
                header.setSizeOfMessage(data.length-i*(sizeOfPacket-header.getSizeOfHeader()));
                listOfPackets.add(newData);
                break;
            }
            i++;
        }
    }

    public void createListPackets(byte[] data,int sizeOfPacket, CharacterTransferData characterTransferData,NameCommand nameCommand,
                                  int countOfPacket,int numberOfPacket,int lengthS ) throws IOException {


        listOfPackets=new ArrayList<>();
        Header header=new Header();
        int i=0;
        header.setInformation(characterTransferData, nameCommand,i,sizeOfPacket,0);

        while(true) {
            byte[] newData=new byte[sizeOfPacket];
            header.setNumberOfPacket(i);

            if((data.length-i*(sizeOfPacket- header.getSizeOfHeader()))>sizeOfPacket-header.getSizeOfHeader()) {
                //  System.out.println(header.getSizeOfHeader());
                header.setSizeOfMessage(lengthS);
                header.setCountOfPacket(countOfPacket);
                header.setNumberOfPacket(numberOfPacket+i);
                System.arraycopy(Header.headerToArrayOfBytes(header), 0, newData, 0, header.getSizeOfHeader());
                System.arraycopy(data, i * (sizeOfPacket-header.getSizeOfHeader()), newData,header.getSizeOfHeader() ,
                        sizeOfPacket-header.getSizeOfHeader());
                header.setSizeOfMessage(sizeOfPacket-header.getSizeOfHeader());
                listOfPackets.add(newData);
                numberOfPacket++;

            }else  {
                header.setSizeOfMessage(lengthS);
                header.setNumberOfPacket(numberOfPacket+i);
                header.setCountOfPacket(countOfPacket);
                System.arraycopy(Header.headerToArrayOfBytes(header),0,newData,0,header.getSizeOfHeader());

                System.arraycopy(data, i * (sizeOfPacket-header.getSizeOfHeader()),newData,header.getSizeOfHeader() ,
                        data.length-i*(sizeOfPacket-header.getSizeOfHeader()) );
                header.setSizeOfMessage(data.length-i*(sizeOfPacket-header.getSizeOfHeader()));
                listOfPackets.add(newData);
                break;
            }
            i++;
        }
    }


    public void echo(byte[] stringRet,DataOutputStream toClient, BufferedReader fromClient ) throws IOException {
        if(listOfPackets!=null)
            listOfPackets.clear();
        createListPackets(stringRet,Controller.sizeOfPackage,CharacterTransferData.setConnection,NameCommand.echo);
        sendPackage(listOfPackets.get(0),toClient);
    }

    public byte[] charArrayToByteArray(char[] beginArray){
        String string=new String(beginArray);
        return string.getBytes();
    }

    public Server() {
    }

    private ArrayList<byte[]> listOfPackets;

    public ArrayList<byte[]> getListOfPackets() {
        return listOfPackets;
    }

    public void createListPackets(byte[] data,int sizeOfPacket, CharacterTransferData characterTransferData,NameCommand nameCommand)
            throws IOException {


        listOfPackets=new ArrayList<byte[]>();
        Header header=new Header();
        int i=0;
        header.setInformation(characterTransferData,nameCommand,i,sizeOfPacket,0);
        while(true) {
            byte[] newData=new byte[sizeOfPacket];
            header.setNumberOfPacket(i);

            if((data.length-i*(sizeOfPacket- header.getSizeOfHeader()))>sizeOfPacket-header.getSizeOfHeader()) {
                //  System.out.println(header.getSizeOfHeader());
                header.setSizeOfMessage(sizeOfPacket-header.getSizeOfHeader());
                System.arraycopy( Header.headerToArrayOfBytes(header), 0,newData, 0, header.getSizeOfHeader());
                System.arraycopy(data, i * (sizeOfPacket-header.getSizeOfHeader()), newData,header.getSizeOfHeader() ,
                        sizeOfPacket-header.getSizeOfHeader());
                header.setSizeOfMessage(sizeOfPacket-header.getSizeOfHeader());
                listOfPackets.add(newData);

            }else  {
                header.setSizeOfMessage(data.length-i*(sizeOfPacket-header.getSizeOfHeader()));
                System.arraycopy(Header.headerToArrayOfBytes(header),0,newData,0,header.getSizeOfHeader());

                System.arraycopy(data, i * (sizeOfPacket-header.getSizeOfHeader()),newData,header.getSizeOfHeader() ,
                        data.length-i*(sizeOfPacket-header.getSizeOfHeader()) );
                header.setSizeOfMessage(data.length-i*(sizeOfPacket-header.getSizeOfHeader()));
                listOfPackets.add(newData);
                break;
            }
            i++;
        }
    }


    int countOfPackets(long length){
        if(length%(Controller.sizeOfPackage-Header.sizeOfHeader)==0)
            return (int)(length/(long)(Controller.sizeOfPackage-Header.sizeOfHeader));
        else return ((int)(length/(long)(Controller.sizeOfPackage-Header.sizeOfHeader))+1);
    }
    public boolean analizePacketFromServer(byte[] arrayFromServer,KitOfHeaders kitOfHeaders) throws IOException, ClassNotFoundException {
        ByteBuffer byteBuffer=ByteBuffer.wrap(arrayFromServer);
        int intCount=byteBuffer.getInt(0);
        if( intCount==0) {
            Header header=Header.arrayOfBytesToHeader(arrayFromServer);
            kitOfHeaders.setHeader(header);
            kitOfHeaders.setTypeHeader(0);
            return true;
        }
        else if(intCount==1){
            SpecialData specialData=SpecialData.arrayOfBytesToSpecialData(arrayFromServer);
            kitOfHeaders.setSpecialData(specialData);
            kitOfHeaders.setTypeHeader(1);
            return true;
        }
        return false;

    }


    public void sendPackage(byte[] array, DataOutputStream toServer) throws IOException {
        toServer.write(array);

    }

    public void readPackage(byte[] array,BufferedReader fromServer) throws IOException {

            char[] arrayRead = new char[Controller.sizeOfPackage / 2];
            fromServer.read(arrayRead);
            System.arraycopy(charArrayToByteArray(arrayRead), 0, array, 0, charArrayToByteArray(arrayRead).length);


    }

    public void getTime(BufferedReader fromClient, DataOutputStream toClient) throws IOException {
        Date date=new Date(System.currentTimeMillis());
        if(listOfPackets!=null)
            listOfPackets.clear();
        createListPackets(date.toString().getBytes(),Controller.sizeOfPackage,CharacterTransferData.transferData,NameCommand.getTime);
        sendPackage(listOfPackets.get(0), toClient);
    }

    public void loadFileUdp(Separator separator, Socket socket) throws IOException, ClassNotFoundException {
        System.out.println("loadFileUdp");

        String fileName = new String(separator.getMessage(), Charset.defaultCharset());

      //  byte[] byteBufferTcp = new byte[Controller.sizeOfPackage];
       // readPackFull(socket, byteBufferTcp);
      //  System.out.println("after readpackFull");
      //  separator.parce(byteBufferTcp);

        int countOfPacketsUdp = separator.getHeader().getCountOfPacket();
        System.out.println("countsOfPacketUdp = " + countOfPacketsUdp);

        int number = separator.getHeader().getNumberOfPacket();
        try ( DatagramSocket dataGramSocket = new DatagramSocket(24001)){


        dataGramSocket.setSoTimeout(22000);
        dataGramSocket.setSendBufferSize(2*Controller.sizeOfPackageUdp);
        dataGramSocket.setReceiveBufferSize(1000*Controller.sizeOfPackageUdp);
        byte[] dataFromClient = new byte[Controller.sizeOfPackageUdp];
        byte[] dataToClient = new byte[Controller.sizeOfPackageUdp];
        File file = new File();
        long pos = separator.getHeader().getNumberOfPacket()*(Controller.sizeOfPackageUdp-HeaderUdp.getSizeOfHeaderUdp());


        for(int i = number; i < countOfPacketsUdp; i++){

            System.out.println(i);
            DatagramPacket datagramPacketReceive = new DatagramPacket(dataFromClient, dataFromClient.length);
            dataGramSocket.receive(datagramPacketReceive);
            InetAddress inetAddress = datagramPacketReceive.getAddress();
            int port = datagramPacketReceive.getPort();

            separator.parceUdp(dataFromClient);

            file.writeInfoInFile(fileName,separator.getMessageUdp(),pos );

            pos += (Controller.sizeOfPackageUdp-HeaderUdp.getSizeOfHeaderUdp());

            System.out.println("SizeOfMessageUdp :" + separator.getHeaderUdp().getSizeOfMessage());

            DatagramPacket datagramPacketSend = new DatagramPacket(dataToClient,dataToClient.length, inetAddress,port );
            dataGramSocket.send(datagramPacketSend);


        }

       // socket.getInputStream().skip(1000000);
        readPackForFile(socket, dataFromClient);
        }

        System.out.println("EndFunctionUdpLoad");
    }

    public void createListPacketsUdp(){

    }

    public void createListPacketsUdp(byte[] data, int sizeOfPacket,int numberOfPacket){


        listOfPackets=new ArrayList<>();
        HeaderUdp headerUdp=new HeaderUdp();
        int i=0;


        while(true) {
            byte[] newData=new byte[sizeOfPacket];
            headerUdp.setNumberOfPacket(i);

            if((data.length-i*(sizeOfPacket- HeaderUdp.getSizeOfHeaderUdp()))>sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp()){

                headerUdp.setSizeOfMessage(sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp());
                headerUdp.setNumberOfPacket(numberOfPacket+i);
                System.arraycopy(HeaderUdp.headerUdpToArrayOfBytes(headerUdp), 0, newData, 0, HeaderUdp.getSizeOfHeaderUdp());
                System.arraycopy(data, i * (sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp()), newData,HeaderUdp.getSizeOfHeaderUdp() ,
                        sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp());
                headerUdp.setSizeOfMessage(sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp());
                listOfPackets.add(newData);
                numberOfPacket++;

            }else  {
                headerUdp.setSizeOfMessage(data.length-i*(sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp()));
                headerUdp.setNumberOfPacket(numberOfPacket+i);
                System.arraycopy(HeaderUdp.headerUdpToArrayOfBytes(headerUdp),0,newData,0, HeaderUdp.getSizeOfHeaderUdp());

                System.arraycopy(data, i * (sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp()),newData,HeaderUdp.getSizeOfHeaderUdp() ,
                        data.length-i*(sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp()) );
                headerUdp.setSizeOfMessage(data.length-i*(sizeOfPacket-HeaderUdp.getSizeOfHeaderUdp()));
                listOfPackets.add(newData);
                break;
            }
            i++;
        }
    }


    public void downloadFileUdp(){

    }

}
