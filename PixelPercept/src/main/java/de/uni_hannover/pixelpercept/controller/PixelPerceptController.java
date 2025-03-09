package de.uni_hannover.pixelpercept.controller;

import de.uni_hannover.pixelpercept.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

/**
 * The overall Controller for the Application.
 * Contains classes for dealing with every triggered user event.
 */
public class PixelPerceptController {

    @FXML
    private Canvas canvas;
    @FXML
    private Text t_result;

    double x_ = -1;
    double y_ = -1;
    NeuralNetwork neuralNetwork;
    boolean drawmode = true; //true=draw , false=erase

    /**
     * Initializes the canvas to draw on with the mouse.
     * Allows for slow
     */
    @FXML
    public void initialize() {
        int inputSize = 784;
        int hiddenSize = 1024;
        int outputSize = 5;
        double learningRate = 0.03;
        String filename = "network_matrices.csv";
        neuralNetwork = new NeuralNetwork(inputSize, hiddenSize, outputSize, learningRate);
        NeuralNetworkIO.loadMatrices(neuralNetwork.weightsInputHidden, neuralNetwork.weightsHiddenOutput, neuralNetwork.biasesHidden, neuralNetwork.biasesOutput, filename);

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK); //rgb(214,40,40));
        double size = 18;

        canvas.setOnMouseDragged(event -> {
            if (x_ < 0 || y_ < 0) {
                x_ = event.getX() - size / 2;
                y_ = event.getY() - size / 2;
            } else if (x_ >= 0 || y_ >= 0) {
                double x = event.getX() - size / 2;
                double y = event.getY() - size / 2;

                Point p1 = new Point((int)x_ , (int)y_); // erster point
                Point p2 = new Point((int)x, (int)y); // zweiter point
                for (double t = 0; t < 1; t+= 0.01) {
                    Point2D p = lineBetween(p1, p2, t);
                    if (drawmode) {
                        g.fillRoundRect(p.getX(), p.getY(), 20, 20, 20, 20);
                        t_result.setText("Drawing...");
                    } else {
                        g.clearRect(p.getX(), p.getY(), 20, 20);
                        t_result.setText("Erasing...");
                    }
                }
                x_ = x;
                y_ = y;
            }
        });

        canvas.setOnMouseClicked(event -> {
            double x = event.getX() - size / 2;
            double y = event.getY() - size / 2;
            if (drawmode) {
                g.fillRoundRect(x, y, 20, 20, 20, 20);
            } else {
                g.clearRect(x, y, 20, 20);
            }
        });
        canvas.setOnMouseReleased(event -> {
            x_ = -1;
            y_ = -1;
            t_result.setText("");
        });
    }

    /**
     * Connects the points made in initialize to lines.
     * Allows for fast and smooth drawing.
     * @param point1
     * @param point2
     * @param time
     * @return
     */
    public Point2D lineBetween(Point point1, Point point2, double time) {
        double deltaX = point2.getX() - point1.getX();
        double deltaY = point2.getY() - point1.getY();
        double x = point1.getX() + time * deltaX;
        double y = point1.getY() + time * deltaY;

        return new Point2D.Double(x, y);
    }

    @FXML
    public void onManual() throws IOException {
        ManualWindow.display();
    }

    /**
     * Exits the Application.
     */
    @FXML
    public void onExit() throws IOException {
        Boolean answer = ConfirmExit.display();
        if (answer) {
            Platform.exit();
        }
    }

    /**
     * Starts the process of recognizing the users drawing and displays the result below the canvas.
     * Connects Controller to Modell by providing a suitable cropped and downscaled image from the canvas for the modell.
     * Receives the probability of what the drawing could be from the model.
     * @param actionEvent
     */
    @FXML
    public void onRecognizeButtonClicked(ActionEvent actionEvent) {
        Image snapshot = canvas.snapshot(null, null);
        int width = (int)snapshot.getWidth();
        int height = (int)snapshot.getHeight();
        int pixels[] = new int[width * height];

        snapshot.getPixelReader().getPixels(0, 0, width, height, (WritablePixelFormat< IntBuffer>) snapshot.getPixelReader().getPixelFormat(),
                pixels, 0, width);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var pixel = pixels[y * width + x];
                int r = (pixel & 0xFF0000) >> 16;
                int g = (pixel & 0xFF00) >> 8;
                int b = (pixel & 0xFF) >> 0;

                bufferedImage.getRaster().setPixel(x, y, new int[]{r, g, b});
            }
        }

        try {
            BufferedImage biggerimage = new BufferedImage((width * 2), (height * 2), BufferedImage.TYPE_INT_RGB);
            Graphics2D bi = (Graphics2D) biggerimage.getGraphics();
            bi.setColor(java.awt.Color.WHITE);
            bi.fillRect ( 0, 0, (width * 2), (height * 2) );
            bi.drawImage(bufferedImage, 300, 300, null);
            bufferedImage = imageCropping(biggerimage, (height * 2), (width * 2));
            bufferedImage = imageDownscale(bufferedImage);
            File outputfile = new File("picture.bmp");
            ImageIO.write(bufferedImage, "bmp", outputfile);
            double[] imageArray = BMPToArrayConverter.getNormalizedPixelValues(bufferedImage);
            double[] output = neuralNetwork.forward(imageArray);

            System.out.println("new image:");
            for (int i = 0; i < output.length; i++) {
                System.out.println(output[i]);
            }

            double maxValue = 0;
            int indexMaxValue = -1;
            for(int i = 0; i < output.length; i++) {
                if(output[i] > maxValue) {
                    maxValue = output[i];
                    indexMaxValue = i;
                }
            }

            if(indexMaxValue == 0) {
                t_result.setText("It´s a bowtie");
            }else if(indexMaxValue == 1) {
                t_result.setText("It´s a cloud");
            }else if(indexMaxValue == 2) {
                t_result.setText("It´s an envelope");
            }else if(indexMaxValue == 3) {
                t_result.setText("It´s a sun");
            }else if(indexMaxValue == 4) {
                t_result.setText("It´s a T-shirt");
            }
        } catch (Exception e) {
            t_result.setText("Error: Draw again!");
            System.out.println("Writing image to file failed: " +e);
        }

        if (!drawmode) {
            drawmode = !drawmode;
        }
    }

    /**
     * Clears the canvas completely when clear button is pressed.
     * @param actionEvent
     */
    @FXML
    public void onClearButtonClicked(ActionEvent actionEvent) {
        clearCanvas();
        t_result.setText("Draw again!");
    }

    @FXML
    public void onButtonSelectedClicked(Event event) {
        Button button = (Button) event.getSource();

        if (drawmode) {
            drawmode = false;
            button.setText("draw");
        } else {
            drawmode = true;
            button.setText("erase");
        }
    }

    /**
     * Clears the canvas.
     */
    public void clearCanvas() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0,0,600,600);
    }

    /**
     * Downscales the image to be used by onRecognizeButtonClicked.
     * Scales the image to 28x28 pixels by default.
     * May be used with big brush size as Graphics2D tends to eliminated pixels otherwise.
     * @param image
     * @return
     */
    public BufferedImage imageDownscale(BufferedImage image) {
        BufferedImage resizedImage = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.drawImage(image, 0, 0, 28, 28, null);
        graphics2D.dispose();
        return resizedImage;
        //return Scalr.resize(image, 28);
    }

    /**
     * Cropps a square piece of the canvas containing the drawing.
     * Allows for better downscaling as the not relevant part of the image is minimized.
     * @param image
     * @param height
     * @param width
     * @return
     */
    public BufferedImage imageCropping(BufferedImage image, int height, int width) {
        int newx = width;
        int newy = height;
        int newwidth = 0;
        int newheight = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(image.getRGB(x, y) < -1 && x < newx){
                    newx = x;
                }
                if(image.getRGB(x, y) < -1 && y < newy){
                    newy = y;
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(image.getRGB(x, y) < -1 && x > newwidth){
                    newwidth = x;
                }
                if(image.getRGB(x, y) < -1 && y > newheight){
                    newheight = y;
                }
            }
        }

        int newerx = newx;
        int newery = newy;

        if(newwidth - newx < newheight - newy){
            newerx = newx - (((newheight - newy) - (newwidth - newx)) / 2);
            newwidth = newwidth + (((newheight - newy) - (newwidth - newx)) / 2);
        } else if (newheight - newy < newwidth - newx) {
            newery = newy - (((newwidth - newx) - (newheight - newy)) / 2);
            newheight = newheight + (((newwidth - newx) - (newheight - newy)) / 2);
        }

        image = image.getSubimage(newerx, newery, (newwidth - newerx), (newheight - newery));
        return image;
    }
}