package de.uni_hannover.pixelpercept.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ManualWindow {

    static Stage window = new Stage();

    public static void display() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ManualWindow.class.getResource("/manual-window.fxml"));

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Manual");

        Scene scene = new Scene(fxmlLoader.load(), 350, 600);

        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
    }

    @FXML
    public void onPLayButtonClicked() {
        window.close();
    }
}
