package org.donntu.knt.mskit.lab4.component;

public class MD4 {
    private static final int BLOCK_LENGTH = 64;
    private int[] context = new int[4];
    private long count;
    private byte[] buffer = new byte[BLOCK_LENGTH];
    private int[] X = new int[16];
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public String hashCode(String string) {
        engineReset();
        engineUpdate(string.getBytes());
        return bytesToHex(engineDigest());
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void engineReset() {
        context[0] = 0x67452301;
        context[1] = 0xEFCDAB89;
        context[2] = 0x98BADCFE;
        context[3] = 0x10325476;
        count = 0L;
        for (int i = 0; i < BLOCK_LENGTH; i++) {
            buffer[i] = 0;
        }
    }


    private void engineUpdate(byte[] input) {
        int bufferNdx = (int) (count % BLOCK_LENGTH);
        count += input.length;
        int partLen = BLOCK_LENGTH - bufferNdx;
        int i = 0;
        if (input.length >= partLen) {
            System.arraycopy(input, 0, buffer, bufferNdx, partLen);
            transform(buffer, 0);
            for (i = partLen; i + BLOCK_LENGTH - 1 < input.length; i += BLOCK_LENGTH) {
                transform(input, i);
            }
            bufferNdx = 0;
        }
        if (i < input.length) {
            System.arraycopy(input, i, buffer, bufferNdx, input.length - i);
        }
    }

    private byte[] engineDigest() {
        int bufferNdx = (int) (count % BLOCK_LENGTH);
        int padLen = (bufferNdx < 56) ? (56 - bufferNdx) : (120 - bufferNdx);
        byte[] tail = new byte[padLen + 8];
        tail[0] = (byte) 0x80;
        for (int i = 0; i < 8; i++) {
            tail[padLen + i] = (byte) ((count * 8) >>> (8 * i));
        }
        engineUpdate(tail);
        byte[] result = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i * 4 + j] = (byte) (context[i] >>> (8 * j));
            }
        }

        return result;
    }

    private void transform(byte[] block, int offset) {
        for (int i = 0; i < 16; i++) {
            X[i] = (block[offset++] & 0xFF) |
                    (block[offset++] & 0xFF) << 8 |
                    (block[offset++] & 0xFF) << 16 |
                    (block[offset++] & 0xFF) << 24;
        }

        int A = context[0];
        int B = context[1];
        int C = context[2];
        int D = context[3];

        A = FF(A, B, C, D, X[0], 3);
        D = FF(D, A, B, C, X[1], 7);
        C = FF(C, D, A, B, X[2], 11);
        B = FF(B, C, D, A, X[3], 19);
        A = FF(A, B, C, D, X[4], 3);
        D = FF(D, A, B, C, X[5], 7);
        C = FF(C, D, A, B, X[6], 11);
        B = FF(B, C, D, A, X[7], 19);
        A = FF(A, B, C, D, X[8], 3);
        D = FF(D, A, B, C, X[9], 7);
        C = FF(C, D, A, B, X[10], 11);
        B = FF(B, C, D, A, X[11], 19);
        A = FF(A, B, C, D, X[12], 3);
        D = FF(D, A, B, C, X[13], 7);
        C = FF(C, D, A, B, X[14], 11);
        B = FF(B, C, D, A, X[15], 19);

        A = GG(A, B, C, D, X[0], 3);
        D = GG(D, A, B, C, X[4], 5);
        C = GG(C, D, A, B, X[8], 9);
        B = GG(B, C, D, A, X[12], 13);
        A = GG(A, B, C, D, X[1], 3);
        D = GG(D, A, B, C, X[5], 5);
        C = GG(C, D, A, B, X[9], 9);
        B = GG(B, C, D, A, X[13], 13);
        A = GG(A, B, C, D, X[2], 3);
        D = GG(D, A, B, C, X[6], 5);
        C = GG(C, D, A, B, X[10], 9);
        B = GG(B, C, D, A, X[14], 13);
        A = GG(A, B, C, D, X[3], 3);
        D = GG(D, A, B, C, X[7], 5);
        C = GG(C, D, A, B, X[11], 9);
        B = GG(B, C, D, A, X[15], 13);

        A = HH(A, B, C, D, X[0], 3);
        D = HH(D, A, B, C, X[8], 9);
        C = HH(C, D, A, B, X[4], 11);
        B = HH(B, C, D, A, X[12], 15);
        A = HH(A, B, C, D, X[2], 3);
        D = HH(D, A, B, C, X[10], 9);
        C = HH(C, D, A, B, X[6], 11);
        B = HH(B, C, D, A, X[14], 15);
        A = HH(A, B, C, D, X[1], 3);
        D = HH(D, A, B, C, X[9], 9);
        C = HH(C, D, A, B, X[5], 11);
        B = HH(B, C, D, A, X[13], 15);
        A = HH(A, B, C, D, X[3], 3);
        D = HH(D, A, B, C, X[11], 9);
        C = HH(C, D, A, B, X[7], 11);
        B = HH(B, C, D, A, X[15], 15);

        context[0] += A;
        context[1] += B;
        context[2] += C;
        context[3] += D;
    }

    private int FF(int a, int b, int c, int d, int x, int s) {
        int t = a + ((b & c) | (~b & d)) + x;
        return t << s | t >>> (32 - s);
    }

    private int GG(int a, int b, int c, int d, int x, int s) {
        int t = a + ((b & (c | d)) | (c & d)) + x + 0x5A827999;
        return t << s | t >>> (32 - s);
    }

    private int HH(int a, int b, int c, int d, int x, int s) {
        int t = a + (b ^ c ^ d) + x + 0x6ED9EBA1;
        return t << s | t >>> (32 - s);
    }
}
