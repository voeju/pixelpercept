package de.uni_hannover.pixelpercept.view;

import de.uni_hannover.pixelpercept.controller.ConfirmExit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * PixelPerceptApplication contains classes for initializing and start the Application.
 */
public class PixelPerceptApplication extends Application {

    Stage primaryStage;

    /**
     * Initializes the Application by accessing the resources directory and creating the scene.
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(PixelPerceptApplication.class.getResource("/PixelPercept-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1050, 750);
        String css = Objects.requireNonNull(this.getClass().getResource("/helloapplication.css")).toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setTitle("PixelPercept");
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            try {
                closeProgram();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Starts the Application.
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    private void closeProgram() throws IOException {
        Boolean answer = ConfirmExit.display();
        if (answer) {
            primaryStage.close();
        }
    }
}