package de.uni_hannover.pixelpercept.model;

public class TestNetwork {

    private static double testNeuralNetwork(double[][] trainingData, double[][] targetData, double[][] output) {

        int correctCount = 0;
        for (int i = 0; i < trainingData.length; i++) {
            // Vergleiche die Vorhersage mit den Zielwerten
            boolean isCorrect = true;
            for (int j = 0; j < targetData[i].length; j++) {
                if (targetData[i][j] != Math.round(output[i][j])) {
                    isCorrect = false;
                    break;
                }
            }

            if (isCorrect) {
                correctCount++;
            }
        }

        // Berechne die Genauigkeit als Verhältnis der korrekten Vorhersagen zur Gesamtanzahl der Vorhersagen
        double accuracy = (double) correctCount / trainingData.length;
        return accuracy;
    }

    public static void main(String[] args) {

        /*
        String filename = "network_matrices.csv";
        int inputSize = 784;
        int hiddenSize = 512;
        int outputSize = 5;
        double learningRate = 0.003;

        //JsonMatricesToArray newMatrices = new JsonMatricesToArray();
        //newMatrices.jsonMatricesToArray();
        NeuralNetwork neuralNetwork = new NeuralNetwork(inputSize, hiddenSize, outputSize, learningRate);
        //NeuralNetworkIO.saveMatrices(newMatrices.weightsHidden, newMatrices.weightsOutput, newMatrices.biasesHidden, newMatrices.biasesOutput, filename);
        NeuralNetworkIO.loadMatrices(neuralNetwork.weightsInputHidden, neuralNetwork.weightsHiddenOutput, neuralNetwork.biasesHidden, neuralNetwork.biasesOutput, filename);
        System.out.println(neuralNetwork.weightsInputHidden[0][0]);


        //////////TEST///////////
        JsonReader jsonTestArray = new JsonReader(100000, 80000);
        jsonTestArray.jsonToArray();
        jsonTestArray.shuffleArray();

        double[][] outputArray = new double[jsonTestArray.targetData.length][jsonTestArray.targetData[0].length];

        for(int i = 0; i < jsonTestArray.trainingData.length; i++) {
            double[] output = neuralNetwork.forward(jsonTestArray.trainingData[i]);
            for(int j = 0; j < jsonTestArray.targetData[0].length; j++) {
                outputArray[i][j] = output[j];
            }
        }

        double accuracy = testNeuralNetwork(jsonTestArray.trainingData, jsonTestArray.targetData, outputArray);
        // Ausgabe der Genauigkeit des neuronalen Netzwerks
        System.out.println("Genauigkeit: " + (accuracy * 100) + "%");

        */
    }
}
