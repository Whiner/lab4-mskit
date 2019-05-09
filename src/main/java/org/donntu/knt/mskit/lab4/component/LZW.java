package org.donntu.knt.mskit.lab4.component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LZW {
    private static final String COMPRESS_EXTENSION = ".lzw";
    private static final int MAX_DICTIONARY_SIZE = 4096;


    public static String compress(String input) throws IOException {
        String output = FileRenamer.getNameCompressedFile(input, COMPRESS_EXTENSION);
        try (
                DataInputStream read = new DataInputStream(new BufferedInputStream(new FileInputStream(input)));
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)))
        ) {

            Map<String, Integer> table = new HashMap<>();
            final int ADDING_COUNT = 256;

            for (int i = 0; i < ADDING_COUNT; i++) {
                table.put(Character.toString((char) i), i);
            }
            int count = ADDING_COUNT;
            byte inputByte;
            StringBuilder temp = new StringBuilder();

            StringBuilder bitTemp = new StringBuilder();

            inputByte = read.readByte();
            int i = new Byte(inputByte).intValue();
            if (i < 0) {
                i += ADDING_COUNT;
            }
            char c = (char) i;
            temp.append(c);

            while (true) {
                try {
                    inputByte = read.readByte();
                } catch (EOFException e) {
                    break;
                }

                i = new Byte(inputByte).intValue();

                if (i < 0) {
                    i += ADDING_COUNT;
                }
                c = (char) i;

                if (table.containsKey(temp.toString() + c)) {
                    temp.append(c);
                } else {
                    bitTemp.append(to12bit(table.get(temp.toString())));

                    while (bitTemp.length() >= 8) {
                        out.writeByte((byte) Integer.parseInt(bitTemp.substring(0, 8), 2));
                        bitTemp.delete(0, 8);
                    }

                    if (count < MAX_DICTIONARY_SIZE) {
                        table.put(temp.append(c).toString(), count++);
                    }
                    temp = new StringBuilder(String.valueOf(c));
                }
            }


            bitTemp.append(to12bit(table.get(temp.toString())));
            while (bitTemp.length() > 0) {
                while (bitTemp.length() < 8) {
                    bitTemp.append('0');
                }
                out.writeByte((byte) Integer.parseInt(bitTemp.substring(0, 8), 2));
                bitTemp.delete(0, 8);
            }

        }
        return output;
    }

    private static String to12bit(int i) {
        StringBuilder temp = new StringBuilder(Integer.toBinaryString(i));
        while (temp.length() < 12) {
            temp.insert(0, "0");
        }
        return temp.toString();
    }

    public static String decompress(String input) throws IOException {
        int charArraySize = MAX_DICTIONARY_SIZE;
        String[] charArray = new String[charArraySize];
        for (int i = 0; i < 256; i++) {
            charArray[i] = Character.toString((char) i);
        }
        int count = 256;

        String output = FileRenamer.getNameDecompressedFile(input, COMPRESS_EXTENSION);

        try (
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(input)));
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)))
        ) {

            int currentWord, priorityWord;
            StringBuilder bitTemp = new StringBuilder();
            readNext(bitTemp, in);
            readNext(bitTemp, in);

            priorityWord = getValue(bitTemp);

            out.writeBytes(charArray[priorityWord]);

            boolean isNotEnd = true;

            while (isNotEnd) {
                try {
                    while (bitTemp.length() < 12) {
                        readNext(bitTemp, in);
                    }
                } catch (EOFException e) {
                    isNotEnd = false;
                }
                if(bitTemp.length() < 12) {
                    break;
                } else {
                    currentWord = getValue(bitTemp);
                }

                if (currentWord >= count) {
                    String s = charArray[priorityWord] + charArray[priorityWord].charAt(0);
                    if (count < charArraySize) {
                        charArray[count] = s;
                    }
                    count++;
                    out.writeBytes(s);
                } else {
                    if (count < charArraySize) {
                        charArray[count] = charArray[priorityWord] + charArray[currentWord].charAt(0);
                    }
                    count++;
                    out.writeBytes(charArray[currentWord]);
                }
                priorityWord = currentWord;
            }

        }
        return output;
    }

    private static int getValue(StringBuilder bitTemp) {
        int i = Integer.parseInt(bitTemp.substring(0, 12), 2);
        bitTemp.delete(0, 12);
        return i;
    }

    private static void readNext(StringBuilder bitTemp, DataInputStream in) throws IOException {
        bitTemp.append(ByteUtils.byteToBits(in.readByte(), 8));
    }
}
