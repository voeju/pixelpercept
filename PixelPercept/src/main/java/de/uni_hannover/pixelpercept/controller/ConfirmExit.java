package de.uni_hannover.pixelpercept.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmExit {

    static boolean answer;
    static Stage window;

    public static boolean display() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConfirmExit.class.getResource("/confirm-exit.fxml"));
        window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Title");

        Scene scene = new Scene(fxmlLoader.load(), 350, 150);

        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

        return answer;
    }

    @FXML
    public void onYesButtonClicked() {
        answer = true;
        window.close();
    }

    @FXML
    public void onNoButtonClicked() {
        answer = false;
        window.close();
    }
}
