package org.donntu.knt.mskit.lab4.controller;

import com.sun.javafx.binding.StringFormatter;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.donntu.knt.mskit.lab4.Main;
import org.donntu.knt.mskit.lab4.service.PrimaryService;

import java.io.IOException;

public class PrimaryController {
    private PrimaryService service = new PrimaryService();

    @FXML
    private AnchorPane primaryPane;

    @FXML
    private Label compressPercentLabel;

    @FXML
    private Button fileDialogButton;

    @FXML
    private TextField filePathTextField;

    private Canvas canvasField;

    @FXML
    private Button randomizeSomeBytesButton;

    @FXML
    void initialize() {
        final Stage currentStage = Main.getCurrentStage();
        compressPercentLabel.setText("неизвестно");
        fileDialogButton.setOnAction(event -> {
            String filename = service.openFileDialog(currentStage);
            if (filename != null) {
                filePathTextField.setText(filename);
                service.processFile();
                service.fillIntegrityLine(canvasField);
                service.fillCompressPercent(compressPercentLabel);
            }
        });
        randomizeSomeBytesButton.setOnAction(event -> {
            service.changeRandomBytesInDecompressedFile();
            service.fillIntegrityLine(canvasField);
        });

        canvasField = new Canvas(765, 45);
        canvasField.setLayoutX(15);
        canvasField.setLayoutY(100);
        canvasField.getGraphicsContext2D().setFill(new Color(0.5, 0.5, 0.5, 1));
        canvasField.getGraphicsContext2D().fillRect(0, 0, canvasField.getWidth(), canvasField.getHeight());
        primaryPane.getChildren().add(canvasField);

    }
}
