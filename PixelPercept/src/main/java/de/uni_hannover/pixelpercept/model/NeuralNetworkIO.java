package de.uni_hannover.pixelpercept.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class NeuralNetworkIO {
    public static void saveMatrices(double[][] weightsInputHidden, double[][] weightsHiddenOutput, double[] biasesHidden, double[] biasesOutput, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Aktuelles Datum abrufen
            Date currentDate = new Date();
            // Datum in einen String umwandeln
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String dateString = dateFormat.format(currentDate);
            writer.write("Version: " + dateString);
            writer.newLine();
            // Uhrzeit in String umwandeln
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String timeString = timeFormat.format(currentDate);
            writer.write("Time: " + timeString);
            writer.newLine();

            // Speichern der Gewichte weightsInputHidden
            for (double[] weightRow : weightsInputHidden) {
                for (double weight : weightRow) {
                    writer.write(String.valueOf(weight));
                    writer.write(",");
                }
                writer.newLine();
            }

            // Leerzeile zur Trennung der Matrizen
            writer.newLine();

            // Speichern der Gewichte weightsHiddenOutput
            for (double[] weightRow : weightsHiddenOutput) {
                for (double weight : weightRow) {
                    writer.write(String.valueOf(weight));
                    writer.write(",");
                }
                writer.newLine();
            }

            // Leerzeile zur Trennung der Matrizen
            writer.newLine();

            // Speichern der Biases biasesHidden
            for (double bias : biasesHidden) {
                writer.write(String.valueOf(bias));
                writer.write(",");
            }

            // Leerzeile zur Trennung der Matrizen
            writer.newLine();

            // Speichern der Biases biasesOutput
            for (double bias : biasesOutput) {
                writer.write(String.valueOf(bias));
                writer.write(",");
            }
            System.out.println("Matrices have been saved...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMatrices(double[][] weightsInputHidden, double[][] weightsHiddenOutput, double[] biasesHidden, double[] biasesOutput, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            //Zeile mit Datum überspringen
            reader.readLine();
            //Zeile mit Uhrzeit überspringen
            reader.readLine();

            // Laden der Gewichte weightsInputHidden
            for (int i = 0; i < weightsInputHidden.length; i++) {
                String[] weightValues = reader.readLine().split(",");
                for (int j = 0; j < weightsInputHidden[i].length; j++) {
                    weightsInputHidden[i][j] = Double.parseDouble(weightValues[j]);
                }
            }

            // Überspringe leere Zeile zwischen den Matrizen
            reader.readLine();

            // Laden der Gewichte weightsHiddenOutput
            for (int i = 0; i < weightsHiddenOutput.length; i++) {
                String[] weightValues = reader.readLine().split(",");
                for (int j = 0; j < weightsHiddenOutput[i].length; j++) {
                    weightsHiddenOutput[i][j] = Double.parseDouble(weightValues[j]);
                }
            }

            // Überspringe leere Zeile zwischen den Matrizen
            reader.readLine();

            // Laden der Biases biasesHidden
            String[] biasValues = reader.readLine().split(",");
            for (int i = 0; i < biasesHidden.length; i++) {
                biasesHidden[i] = Double.parseDouble(biasValues[i]);
            }

            // Laden der Biases biasesOutput
            biasValues = reader.readLine().split(",");
            for (int i = 0; i < biasesOutput.length; i++) {
                biasesOutput[i] = Double.parseDouble(biasValues[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
