package org.donntu.knt.mskit.lab4.component;

public class ByteUtils {
    public static String byteToBits(byte b, int size) {
        return String.format("%" + size + "s", Integer.toBinaryString(b & 0xFF))
                .replace(' ', '0');
    }
}
