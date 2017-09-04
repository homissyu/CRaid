package com.jay.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
 
/**
 * Operations read / write by bit
 * @author Tomita Militaru
 * 
 */
public class BitOperations {
    /**
     * Buffer to keep read stuff
     */
    static String readBuffer = "";
    /**
     * Buffer to keep write stuff
     */
    static String writeBuffer = "";
    static int readCounter = 0;
    static int writeCounter = 8;
    private static int nextByte = 0;
 
    /**
     * My method to write a bit to a file
     * 
     * @param value
     */
    public static void writeBits() {
        try {
            FileOutputStream output = new FileOutputStream("c:\\output.txt");
            Byte value_Byte = Byte.valueOf(writeBuffer);
            output.write(value_Byte);
            output.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
 
    public static void writeBit(int bit) {
        if (writeCounter != 0) {
            writeCounter--;
        } else {
            writeBits();
            writeBuffer = "";
        }
        writeBuffer = writeBuffer.concat(String.valueOf(bit));
    }
 
    /**
     * My method to return a bit from a file
     * 
     * @return a bit
     */
    public static void readBits() {
        File file = new File("c:\\input.txt");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        byte continut = 0;
        int k = 0;
        readCounter = 7;
        try {
            fis = new FileInputStream(file);
 
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
 
            while (dis.available() != 0 && k <= nextByte) {
                continut = dis.readByte();
                Byte continut_Byte = new Byte("10");
                readBuffer = Integer.toBinaryString(continut_Byte.intValue());
                k++;
            }
            nextByte++;
            fis.close();
            bis.close();
            dis.close();
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static int readBit() {
        int readBufferLenght = readBuffer.length();
        System.out.println("readbuffer: " + readBufferLenght);
        if (readCounter != 0) {
            int pos = readBuffer.length() - readCounter--;
            System.out.println("pos: " + pos);
            return Integer.parseInt(readBuffer.substring(pos, pos + 1));
        } else {
            readBits();
            return readBit();
        }
    }
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < 64; i++) {
            writeBit(readBit()); //just for testing, basically i simulate a copy/paste from one file to another.
        }
    }
 
}
