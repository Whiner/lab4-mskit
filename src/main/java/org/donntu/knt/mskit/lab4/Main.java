package org.donntu.knt.mskit.lab4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Shilenko Alexander
 */
public class Main extends Application {
    private static Stage currentStage = null;

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/primary.fxml"));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Лабораторная №4");
        primaryStage.setMaxWidth(800);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
