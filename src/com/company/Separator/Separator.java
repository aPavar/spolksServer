package com.company.Separator;

import com.company.Header.Header;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by apava on 27.09.2016.
 */
public class Separator {
    Header header;
    byte[] message;
    boolean isTelnet;
    boolean isProtocol;
    boolean isUnknown;
    String telnetCommand;
    String messageTelnet;

    public String getMessageTelnet() {
        return messageTelnet;
    }

    public void setMessageTelnet(String messageTelnet) {
        this.messageTelnet = messageTelnet;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public Separator(Header header, byte[] message) {
        this.header = header;
        this.message = message;
    }

    public Separator(){

    }

    public boolean isTelnet() {
        return isTelnet;
    }

    public void setIsTelnet(boolean isTelnet) {
        this.isTelnet = isTelnet;
    }

    public String getTelnetComand() {
        return telnetCommand;
    }

    public void setTelnetComand(String telnetComand) {
        this.telnetCommand = telnetComand;
    }

    public void parce(byte[] array) throws IOException, ClassNotFoundException {
        String string=new String(array, Charset.defaultCharset());
        isProtocol=Header.isHeader(array);
        if(isProtocol){
            header = Header.arrayOfBytesToHeader(array);
            message = new byte[header.getSizeOfMessage()];
            System.arraycopy(array, Header.sizeOfHeader, message, 0, message.length);
        }else{
            int i=string.indexOf("echo");
            if(i==-1){
                i=string.indexOf("time");
                if(i!=0) {
                    isUnknown=true;
                }
                else{
                    telnetCommand="time";
                    isTelnet=true;
                }
            }else{
                if(i==0) {
                    telnetCommand = "echo";
                    isTelnet = true;
                    message = new byte[array.length - 5];
                    System.arraycopy(array, 5, message, 0, array.length - 5);
                    messageTelnet = new String(message, Charset.defaultCharset());
                }
            }
        }
    }

    public boolean isProtocol() {
        return isProtocol;
    }

    public boolean isUnknown() {
        return isUnknown;
    }

    public String getTelnetCommand() {
        return telnetCommand;
    }
}
