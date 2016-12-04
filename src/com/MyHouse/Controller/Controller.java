package com.MyHouse.Controller;

import com.MyHouse.CharacterTransferData.CharacterTransferData;
import com.MyHouse.ControllerLoadFile;
import com.MyHouse.File.File;
import com.MyHouse.Header.Header;
import com.MyHouse.Separator.Separator;
import com.MyHouse.StructureClientLoad.StructureClientLoad;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dmitry on 27.11.2016.
 */
public class Controller {
   private static int port = 6000;
   private static  String ip = "192.168.57.2";

   private static String clientChannel = "clientChannel";
   private static String serverChannel = "sererChannel";
   private static String channelType = "channelType";


   public static void move() throws IOException, ClassNotFoundException, InterruptedException {
       int port = 6000;
       int sizeOfPackage = 1024;
       int count = 0;
       ControllerLoadFile controllerLoadFile = new ControllerLoadFile();

       ServerSocketChannel channel = ServerSocketChannel.open();

       channel.bind(new InetSocketAddress(ip, port));

       channel.configureBlocking(false);

       Selector selector = Selector.open();


       SelectionKey socketServerSelectionKey = channel.register(selector,
               SelectionKey.OP_ACCEPT);

       Map<String, String> properties = new HashMap<String, String>();
       properties.put(channelType, serverChannel);
       socketServerSelectionKey.attach(properties);

       for (;;) {

           if (selector.select() == 0)
               continue;
           // the select method returns with a list of selected keys
           Set<SelectionKey> selectedKeys = selector.selectedKeys();
           Iterator<SelectionKey> iterator = selectedKeys.iterator();
           while (iterator.hasNext()) {
               SelectionKey key = iterator.next();

               if (((Map) key.attachment()).get(channelType).equals(
                       serverChannel)) {
                   ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
                           .channel();
                   SocketChannel clientSocketChannel = serverSocketChannel
                           .accept();
                   clientSocketChannel.socket().setReceiveBufferSize(10000*sizeOfPackage);

                   if (clientSocketChannel != null) {
                       clientSocketChannel.configureBlocking(false);
                       SelectionKey clientKey = clientSocketChannel.register(
                               selector, SelectionKey.OP_READ,
                               SelectionKey.OP_WRITE);
                       Map<String, String> clientProperties = new HashMap<String, String>();
                       clientProperties.put(channelType, clientChannel+String.valueOf(count));
                       clientKey.attach(clientProperties);

                       StructureClientLoad structureClientLoad = new StructureClientLoad();
                       structureClientLoad.setNameOfClient("clientChannel"+String.valueOf(count));
                       controllerLoadFile.getBase().put("clientChannel"+String.valueOf(count),structureClientLoad);
                       count++;
                   }

               } else {

                   String clientDef = ((Map) key.attachment()).get(channelType).toString();
                   SocketChannel clientChannel = (SocketChannel) key.channel();
                   Separator separator = new Separator();
                   byte [] dataFromClient = new byte[sizeOfPackage];
                   int bytesRead = 0;
                   if (key.isReadable()) {
                       int a=clientChannel.read(ByteBuffer.wrap(dataFromClient));




                       separator.parce(dataFromClient);
                       System.out.println(separator.getHeader().getNumberOfPacket());
                       if(separator.getHeader().getCharacterTransferData().
                               equals(CharacterTransferData.setConnection))
                           controllerLoadFile.getBase().get(clientDef).setNameOfFile
                                   (new String(separator.getMessage(),Charset.defaultCharset()));
                       else if(separator.getHeader().getCharacterTransferData().
                               equals(CharacterTransferData.transferData)){
                         // File file = new File(controllerLoadFile.getBase().get(clientDef).getNameOfFile());
                           File file = new File();
                       //   file.writeInfoInFile(controllerLoadFile.getBase().get(clientDef).getNameOfFile(),);
                           file.writeInfoInFileSpec(controllerLoadFile.getBase().
                                   get(clientDef).getNameOfFile(),separator.getMessage(),
                                   separator.getHeader().getNumberOfPacket()*(sizeOfPackage- Header.sizeOfHeader),
                                   separator.getHeader().getSizeOfMessage());
                       }

                   }

               }

               iterator.remove();

           }
       }

   }

  /* public static void move2() throws IOException {
       int port = 6000;

       // create a new serversocketchannel. The channel is unbound.
       ServerSocketChannel channel = ServerSocketChannel.open();

       channel.bind(new InetSocketAddress(ip, port));

       channel.configureBlocking(false);

       Selector selector = Selector.open();


       SelectionKey socketServerSelectionKey = channel.register(selector,
               SelectionKey.OP_ACCEPT);

       Map<String, String> properties = new HashMap<String, String>();
       properties.put(channelType, serverChannel);
       socketServerSelectionKey.attach(properties);

       for (;;) {

           if (selector.select() == 0)
               continue;
           // the select method returns with a list of selected keys
           Set<SelectionKey> selectedKeys = selector.selectedKeys();
           Iterator<SelectionKey> iterator = selectedKeys.iterator();
           while (iterator.hasNext()) {
               SelectionKey key = iterator.next();

               if (((Map) key.attachment()).get(channelType).equals(
                       serverChannel)) {

                   ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
                           .channel();
                   SocketChannel clientSocketChannel = serverSocketChannel
                           .accept();

                   if (clientSocketChannel != null) {
                       clientSocketChannel.configureBlocking(false);
                       SelectionKey clientKey = clientSocketChannel.register(
                               selector, SelectionKey.OP_READ,
                               SelectionKey.OP_WRITE);
                       Map<String, String> clientproperties = new HashMap<String, String>();
                       clientproperties.put(channelType, clientChannel);
                       clientKey.attach(clientproperties);


                       CharBuffer buffer = CharBuffer.wrap("Hello client");
                       while (buffer.hasRemaining()) {
                           clientSocketChannel.write(Charset.defaultCharset()
                                   .encode(buffer));

                       }
                       buffer.clear();
                   }

               } else {

                   ByteBuffer buffer = ByteBuffer.allocate(20);
                   SocketChannel clientChannel = (SocketChannel) key.channel();
                   int bytesRead = 0;
                   if (key.isReadable()) {
                       // the channel is non blocking so keep it open till the
                       // count is >=0
                       if ((bytesRead = clientChannel.read(buffer)) > 0) {
                           buffer.flip();
                           System.out.println(Charset.defaultCharset().decode(
                                   buffer));
                           buffer.clear();
                       }
                       if (bytesRead < 0) {

                           clientChannel.close();
                       }
                   }

               }

               iterator.remove();

           }
       }
   }*/



}
