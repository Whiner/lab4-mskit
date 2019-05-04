package org.donntu.knt.mskit.lab4.component;

/**
 * @author Shilenko Alexander
 */
public class FileRenamer {
    public static String getNameCompressedFile(String filename, String compressExtension) {
        return filename + compressExtension;
    }

    public static String getNameDecompressedFile(String filename, String compressExtension) {
        filename = filename.replace('\\', '/');
        String[] split = filename.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            stringBuilder.append(split[i]).append('/');
        }
        stringBuilder.append("decompressed_").append(split[split.length - 1]);
        return stringBuilder.toString().replace(compressExtension, "");
    }
}
