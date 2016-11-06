package com.company.File;

import com.company.StructureOfReturnValue.StructureOfReturnValue;

import java.io.*;

/**
 * Created by apava on 25.09.2016.
 */
public class File {
    String nameOfFile;
    long sizeOfFile;
    long getSizeOfFile(String nameOfFile) throws IOException {
        RandomAccessFile file=new RandomAccessFile(nameOfFile,"r");
        long length=file.length();
        file.close();
        return length;

    }

    public File(String nameOfFile) throws IOException {
        this.nameOfFile = nameOfFile;
        RandomAccessFile file=new RandomAccessFile(nameOfFile,"r");
        sizeOfFile= file.length();
        file.close();
    }
    public File() {

    }

    public String getNameOfFile() {

        return nameOfFile;
    }

    public void setNameOfFile(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }

    public long getSizeOfFile() {
        return sizeOfFile;
    }

    public void setSizeOfFile(long sizeOfFile) {
        this.sizeOfFile = sizeOfFile;
    }

    public StructureOfReturnValue readInfoFromFile(String nameOfFile, int sizeOfInfo, long position, byte[] array) throws IOException {
        RandomAccessFile file=new RandomAccessFile(nameOfFile,"r");
        file.seek(position);
        StructureOfReturnValue structureOfReturnValue;
        long fileLength=file.length();
        for(int i=0;i<sizeOfInfo;i++){
            if(fileLength==position+i) {
                file.close();
                return new StructureOfReturnValue(true,i);
            }
            array[i]=file.readByte();
        }
        return new StructureOfReturnValue(false,sizeOfInfo);
    }

    public void writeInfoInFile(String nameOfFile,byte[] array,long position) throws FileNotFoundException {

        try(RandomAccessFile randomAccessFile=new RandomAccessFile(nameOfFile,"rw")){
            randomAccessFile.seek(position);
            randomAccessFile.write(array);
            randomAccessFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeInfoInFileSpec(String nameOfFile,byte[] array,long position, int howMuch){
        try(RandomAccessFile randomAccessFile=new RandomAccessFile(nameOfFile,"rw")){
            randomAccessFile.seek(position);
            randomAccessFile.write(array,0,howMuch);
            randomAccessFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getOnlyNameOfFile(String path){
        int pos=0;
        for(int i=path.length();i>=0;i--)
            if( path.getBytes()[i]==java.io.File.pathSeparator.getBytes()[0]) {
                pos = i;
                break;
            }
        return path.substring(pos);
    }
}
