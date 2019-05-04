package org.donntu.knt.mskit.lab4.service;

import com.sun.javafx.binding.StringFormatter;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.donntu.knt.mskit.lab4.component.LZW;
import org.donntu.knt.mskit.lab4.component.MD4;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PrimaryService {
    private final MD4 md4 = new MD4();
    private String filename;
    private String compressedFilename;
    private String decompressedFilename;
    private final static int BLOCKS_COUNT = 10;

    public String openFileDialog(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Файлы", "*.txt", "*.bmp")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            filename = file.getAbsolutePath();
            return filename;
        } else {
            return null;
        }
    }

    public void processFile() {
        try {
            compressedFilename = LZW.compress(filename);
            decompressedFilename = LZW.decompress(compressedFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void fillIntegrityLine(Canvas canvas) {
        List<Boolean> fileIntegrity = getFileIntegrity(filename, decompressedFilename);
        if (!fileIntegrity.isEmpty()) {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            double separatorWidth = 2;
            double step = (width - BLOCKS_COUNT * separatorWidth) / fileIntegrity.size();
            double x = 0;
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            for (int i = 0; i < fileIntegrity.size(); i++) {
                final Boolean isOk = fileIntegrity.get(i);
                if (isOk) {
                    graphicsContext.setFill(new Color(0.1, 0.7, 0.1, 1.0));
                } else {
                    graphicsContext.setFill(new Color(0.9, 0.1, 0.1, 1.0));
                }
                graphicsContext.fillRect(x, 0, step, height);
                x += step;
                if(i < fileIntegrity.size() - 1) {
                    graphicsContext.setFill(new Color(0.0, 0.0, 0.0, 1.0));
                }
                graphicsContext.fillRect(x, 0, separatorWidth, height);
                x += separatorWidth;
            }
        }
    }

    private List<Boolean> getFileIntegrity(String originalFile, String decompressedFile) {
        try (
                RandomAccessFile originalFileReader = new RandomAccessFile(originalFile, "r");
                RandomAccessFile decompressedFileReader = new RandomAccessFile(decompressedFile, "r")
        ) {
            int blockSize = (int) Math.ceil(originalFileReader.length() / BLOCKS_COUNT);
            List<Boolean> equalsBytesList = new LinkedList<>();
            byte[] bytes = new byte[blockSize];
            for (int i = 0; i < BLOCKS_COUNT; i++) {
                if (i == BLOCKS_COUNT - 1) {
                    bytes = new byte[(int) (originalFileReader.length() - originalFileReader.getFilePointer())];
                }
                originalFileReader.read(bytes);
                String originalHashCode = md4.hashCode(new String(bytes));
                decompressedFileReader.read(bytes);
                String decompressedHashCode = md4.hashCode(new String(bytes));
                equalsBytesList.add(originalHashCode.equals(decompressedHashCode));
            }
            return equalsBytesList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void changeRandomBytesInDecompressedFile(int count) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(decompressedFilename, "rw")) {
            if(count < 1 || count > file.length()) {
                throw new Exception(
                        "Некорректное количество заменяемых байт по отношению к размеру файла " +
                                "(" + file.length() + ")");
            }
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < count; i++) {
                int position = random.nextInt((int) file.length());
                int newByte;
                file.seek(position);
                int oldByte = file.read();
                do {
                    newByte = random.nextInt(255);
                } while (newByte == oldByte);
                file.write(newByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fillCompressPercent(Label compressPercentLabel) {
        double compressPercent = compressPercent();
        compressPercentLabel.setText(StringFormatter.format("%3.2f", compressPercent).getValue() + "%");
    }

    private double compressPercent() {
        return 100 - ((double) new File(compressedFilename).length() / (double) new File(filename).length() * 100.0);
    }
}
