package org.donntu.knt.mskit.lab4.controller;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
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
    private TextField changeBytesCount;

    @FXML
    void initialize() {
        final Stage currentStage = Main.getCurrentStage();
        compressPercentLabel.setText("неизвестно");
        changeBytesCount.setText("20");
        randomizeSomeBytesButton.setDisable(true);
        changeBytesCount.setDisable(true);
        fileDialogButton.setOnAction(event -> {
            String filename = service.openFileDialog(currentStage);
            if (filename != null) {
                filePathTextField.setText(filename);
                service.processFile();
                service.fillIntegrityLine(canvasField);
                service.fillCompressPercent(compressPercentLabel);
                randomizeSomeBytesButton.setDisable(false);
                changeBytesCount.setDisable(false);
            }
        });
        randomizeSomeBytesButton.setOnAction(event -> {
            try {
                int count = Integer.parseInt(changeBytesCount.getText());
                service.changeRandomBytesInDecompressedFile(count);
                service.fillIntegrityLine(canvasField);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        changeBytesCount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                changeBytesCount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        canvasField = new Canvas(765, 45);
        canvasField.setLayoutX(15);
        canvasField.setLayoutY(100);
        canvasField.getGraphicsContext2D().setFill(new Color(0.5, 0.5, 0.5, 1));
        canvasField.getGraphicsContext2D().fillRect(0, 0, canvasField.getWidth(), canvasField.getHeight());
        primaryPane.getChildren().add(canvasField);

    }
}
