package org.donntu.knt.mskit.lab4.component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LZW {
    public static final String COMPRESS_EXTENSION = ".lzw";

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

            byte[] buffer = new byte[3];
            boolean onLeft = true;

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
                    String s12 = to12bit(table.get(temp.toString()));

                    if (onLeft) {
                        buffer[0] = (byte) Integer.parseInt(s12.substring(0, 8), 2);
                        buffer[1] = (byte) Integer.parseInt(s12.substring(8, 12) + "0000", 2);
                    } else {
                        bufferWork(out, buffer, s12);
                    }
                    onLeft = !onLeft;
                    if (count < 4096) {
                        table.put(temp.append(c).toString(), count++);
                    }
                    temp = new StringBuilder(String.valueOf(c));
                }
            }


            String temp12Bit = to12bit(table.get(temp.toString()));
            if (onLeft) {
                buffer[0] = (byte) Integer.parseInt(temp12Bit.substring(0, 8), 2);
                buffer[1] = (byte) Integer.parseInt(temp12Bit.substring(8, 12) + "0000", 2);
                out.writeByte(buffer[0]);
                out.writeByte(buffer[1]);
            } else {
                bufferWork(out, buffer, temp12Bit);
            }

        }
        return output;
    }

    private static void bufferWork(DataOutputStream out, byte[] buffer, String s12) throws IOException {
        buffer[1] += (byte) Integer.parseInt(s12.substring(0, 4), 2);
        buffer[2] = (byte) Integer.parseInt(s12.substring(4, 12), 2);
        for (int b = 0; b < buffer.length; b++) {
            out.writeByte(buffer[b]);
            buffer[b] = 0;
        }
    }

    private static String to12bit(int i) {
        StringBuilder temp = new StringBuilder(Integer.toBinaryString(i));
        while (temp.length() < 12) {
            temp.insert(0, "0");
        }
        return temp.toString();
    }

    private static int getValue(byte b1, byte b2, boolean onLeft) {
        StringBuilder temp1 = new StringBuilder(Integer.toBinaryString(b1));
        StringBuilder temp2 = new StringBuilder(Integer.toBinaryString(b2));
        while (temp1.length() < 8) {
            temp1.insert(0, "0");
        }
        if (temp1.length() == 32) {
            temp1 = new StringBuilder(temp1.substring(24, 32));
        }
        while (temp2.length() < 8) {
            temp2.insert(0, "0");
        }
        if (temp2.length() == 32) {
            temp2 = new StringBuilder(temp2.substring(24, 32));
        }

        if (onLeft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }

    }

    public static String decompress(String input) throws IOException {
        int charArraySize = 4096;
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
            byte[] buffer = new byte[3];
            boolean onLeft;

            buffer[0] = in.readByte();
            buffer[1] = in.readByte();

            priorityWord = getValue(buffer[0], buffer[1], true);
            onLeft = false;
            out.writeBytes(charArray[priorityWord]);

            while (true) {
                try {
                    if (onLeft) {
                        buffer[0] = in.readByte();
                        buffer[1] = in.readByte();
                        currentWord = getValue(buffer[0], buffer[1], true);
                    } else {
                        buffer[2] = in.readByte();
                        currentWord = getValue(buffer[1], buffer[2], false);
                    }
                } catch (EOFException e) {
                    break;
                }
                onLeft = !onLeft;
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
}
