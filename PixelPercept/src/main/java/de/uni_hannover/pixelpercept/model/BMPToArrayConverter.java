package de.uni_hannover.pixelpercept.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
public class BMPToArrayConverter {
    public static void main(String[] args) {
        try {
            // 1. Lade die BMP-Bilddatei
            File bmpFile = new File("picture2.bmp");
            BufferedImage image = ImageIO.read(bmpFile);

            // 2. Extrahiere die Graustufenpixelwerte

            double[] normalizedPixelValues = getNormalizedPixelValues(image);

            // Hier kannst du das Array normalizedPixelValues weiterverwenden, z.B. um es in ein neuronales Netzwerk einzuspeisen.
            String filename = "network_matrices.csv";

            ////////////// Matrizen aus der Datei laden /////////////
            int inputSize = 784;
            int hiddenSize = 1024;
            int outputSize = 5;
            double learningRate = 0.03;

            NeuralNetwork neuralNetwork = new NeuralNetwork(inputSize, hiddenSize, outputSize, learningRate);
            NeuralNetworkIO.loadMatrices(neuralNetwork.weightsInputHidden, neuralNetwork.weightsHiddenOutput, neuralNetwork.biasesHidden, neuralNetwork.biasesOutput, filename);
            System.out.println(neuralNetwork.weightsInputHidden[0][0]);


            double[] output = neuralNetwork.forward(normalizedPixelValues);
            System.out.println("Output: " + Arrays.toString(output));
            System.out.println();
            System.out.println("Input: " + Arrays.toString(normalizedPixelValues));
            System.out.println();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[] getNormalizedPixelValues(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[] normalizedPixelValues = new double[width * height];
        int maxGrayValue = 255; // Maximaler Graustufenwert

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int gray = (rgb >> 16) & 0xFF; // Nehme den Rot-Kanal als Graustufenwert
                double normalizedGray = 1.0 - (gray / (double) maxGrayValue); // Normalisiere den Graustufenwert
                normalizedPixelValues[y * width + x] = normalizedGray;
            }
        }

        return normalizedPixelValues;
    }
}
