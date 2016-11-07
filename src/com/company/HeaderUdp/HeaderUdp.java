package com.company.HeaderUdp;

import java.nio.ByteBuffer;

/**
 * Created by Dmitry on 06.11.2016.
 */
public class HeaderUdp {
    private  int numberOfPacket;
    private int sizeOfMessage;

    private static int sizeOfHeaderUdp=8;

    public int getNumberOfPacket() {
        return numberOfPacket;
    }

    public void setNumberOfPacket(int numberOfPacket) {
        this.numberOfPacket = numberOfPacket;
    }

    public int getSizeOfMessage() {
        return sizeOfMessage;
    }

    public void setSizeOfMessage(int sizeOfMessage) {
        this.sizeOfMessage = sizeOfMessage;
    }

    public static byte[] headerUdpToArrayOfBytes(HeaderUdp headerUdp){

        byte[] array=new byte[sizeOfHeaderUdp];
        System.arraycopy(ByteBuffer.allocate(4).putInt(headerUdp.numberOfPacket).array(),0,array,0,4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(headerUdp.sizeOfMessage).array(),0,array,4,4);

        return array;
    }

    public static HeaderUdp arrayOfBytesToHeaderUdp(byte[] arrayOfBytes){

        HeaderUdp headerUdp=new HeaderUdp();
        ByteBuffer byteBuffer=ByteBuffer.wrap(arrayOfBytes);
        headerUdp.setNumberOfPacket(byteBuffer.getInt(0));
        headerUdp.setSizeOfMessage(byteBuffer.getInt(4));

        return headerUdp;
    }

    public static int getSizeOfHeaderUdp() {
        return sizeOfHeaderUdp;
    }

    public static void setSizeOfHeaderUdp(int sizeOfHeaderUdp) {
        HeaderUdp.sizeOfHeaderUdp = sizeOfHeaderUdp;
    }

    public HeaderUdp(){

    }
}
