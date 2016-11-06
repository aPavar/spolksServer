package com.company.SpecialData;

import com.company.CharacterTransferData.CharacterTransferData;
import com.company.NameCommand.NameCommand;

import java.nio.ByteBuffer;

/**
 * Created by apava on 25.09.2016.
 */
public class SpecialData {
    int typePacket;////0 normal header  1-specialData
    CharacterTransferData characterTransferData;
    NameCommand nameCommand;
    int countOfPackets;
    public static int size=24;
    int numberOfPacket;
    int sizeOfMessage;

    public int getSizeOfMessage() {
        return sizeOfMessage;
    }

    public void setSizeOfMessage(int sizeOfMessage) {
        this.sizeOfMessage = sizeOfMessage;
    }

    public NameCommand getNameCommand() {
        return nameCommand;
    }

    public int getNumberOfPacket() {
        return numberOfPacket;
    }

    public void setNumberOfPacket(int numberOfPacket) {
        this.numberOfPacket = numberOfPacket;
    }

    public void setNameCommand(NameCommand nameCommand) {

        this.nameCommand = nameCommand;
    }

    public CharacterTransferData getCharacterTransferData() {
        return characterTransferData;
    }

    public void setCharacterTransferData(CharacterTransferData characterTransferData) {
        this.characterTransferData = characterTransferData;
    }

    public int getCountOfPackets() {
        return countOfPackets;
    }

    public void setCountOfPackets(int countOfPackets) {
        this.countOfPackets = countOfPackets;
    }

    public byte[] specialDataToArrayOfBytes(){
        byte[] array=new byte[SpecialData.size];
        System.arraycopy(ByteBuffer.allocate(4).putInt(this.typePacket).array(),0,array,0,4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(this.characterTransferData.ordinal()).array(),0,array,4,4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(this.nameCommand.ordinal()).array(),0,array,8,4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(this.countOfPackets).array(),0,array,12,4);
    //    System.arraycopy(ByteBuffer.allocate(4).putInt(SpecialData.size).array(),0,array,16,4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(this.numberOfPacket).array(),0,array,16,4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(this.sizeOfMessage).array(),0,array,20,4);
        return array;

    }
    public SpecialData(){

    }

    public SpecialData(int typePacket, CharacterTransferData characterTransferData, NameCommand nameCommand, int countOfPackets,
                       int numberOfPacket) {
        this.typePacket = typePacket;
        this.characterTransferData = characterTransferData;
        this.nameCommand = nameCommand;
        this.countOfPackets = countOfPackets;
        this.numberOfPacket=numberOfPacket;
    }

    static public SpecialData arrayOfBytesToSpecialData(byte[] arrayOfBytes){
        SpecialData specialData=new SpecialData();
        byte[] array=new byte[4];

        ByteBuffer byteBuffer=ByteBuffer.wrap(arrayOfBytes);
        int intCount=byteBuffer.getInt(0);

        specialData.typePacket=intCount;
        if(specialData.typePacket==1) {


            intCount = byteBuffer.getInt(4);

            switch (intCount) {
                case 1:
                    specialData.characterTransferData = CharacterTransferData.setConnection;
                    break;
                case 2:
                    specialData.characterTransferData = CharacterTransferData.transferData;
                    break;
                case 3:
                    specialData.characterTransferData = CharacterTransferData.closingConnection;
                    break;
                case 4:
                    specialData.characterTransferData = CharacterTransferData.command;
            }

            //    byteBuffer.wrap(array);
            intCount = byteBuffer.getInt(8);
            switch (intCount) {
                case 1:
                    specialData.nameCommand = NameCommand.noCommand;
                    break;
                case 2:
                    specialData.nameCommand = NameCommand.echo;
                    break;
                case 3:
                    specialData.nameCommand = NameCommand.getTime;
            }

            specialData.countOfPackets = byteBuffer.getInt(12);
            specialData.numberOfPacket=byteBuffer.getInt(16);
            specialData.sizeOfMessage=byteBuffer.getInt(20);
            return specialData;
        }else return null;
    }





}
