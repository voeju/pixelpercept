package de.uni_hannover.pixelpercept.model;

import java.util.Random;
import java.lang.Math;

public class NeuralNetwork {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    private double learningRate;
    static int batchIterate = 0;

    public double[][] weightsInputHidden;
    public double[][] weightsHiddenOutput;
    public double[] biasesHidden;
    public double[] biasesOutput;

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;

        weightsInputHidden = new double[inputSize][hiddenSize];
        weightsHiddenOutput = new double[hiddenSize][outputSize];
        biasesHidden = new double[hiddenSize];
        biasesOutput = new double[outputSize];

        initializeWeights();
    }

    private void initializeWeights() {
        Random random = new Random();

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] = random.nextGaussian();
            }
        }

        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenOutput[i][j] = random.nextGaussian();
            }
        }

        for (int i = 0; i < hiddenSize; i++) {
            biasesHidden[i] = random.nextGaussian();
        }

        for (int i = 0; i < outputSize; i++) {
            biasesOutput[i] = random.nextGaussian();
        }

    }

    //private double relu(double x) {
    //    return Math.max(0, x);
    //}

    //////////// GETTER ////////////////
    public double[][] getWeightsIH() {
        return this.weightsInputHidden;
    }
    public double[][] getWeightsHO() {
        return this.weightsHiddenOutput;
    }
    public double[] getBiasesH() {
        return this.biasesHidden;
    }
    public double[] getBiasesO() {
        return this.biasesOutput;
    }

    //ist nicht mehr relu sondern tanh!
    private double relu(double x) {
        return Math.tanh(x);
    }

    public double tanhDerivative(double x) {
        double tanhX = Math.tanh(x);
        return 1 - Math.pow(tanhX, 2);
    }

    private double[] softmax(double[] x) {
        double[] softmaxOutputs = new double[x.length];
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < x.length; i++) {
            if (x[i] > max) {
                max = x[i];
            }
        }

        double sumExp = 0.0;
        for (int i = 0; i < x.length; i++) {
            softmaxOutputs[i] = Math.exp(x[i]);
            sumExp += softmaxOutputs[i];
        }

        for (int i = 0; i < x.length; i++) {
            softmaxOutputs[i] /= sumExp;
        }

        return softmaxOutputs;
    }

    public double[] forward(double[] inputs) {
        double[] hiddenLayer = new double[hiddenSize];
        double[] outputLayer = new double[outputSize];

        // Calculate values for the hidden layer
        for (int i = 0; i < hiddenSize; i++) {
            double sum = biasesHidden[i];
            for (int j = 0; j < inputSize; j++) {
                sum += inputs[j] * weightsInputHidden[j][i];
            }
            hiddenLayer[i] = relu(sum);
        }

        // Calculate values for the output layer
        for (int i = 0; i < outputSize; i++) {
            double sum = biasesOutput[i];
            for (int j = 0; j < hiddenSize; j++) {
                sum += hiddenLayer[j] * weightsHiddenOutput[j][i];
            }
            outputLayer[i] = sum;
        }
        //System.out.println("Outputlayer: " + Arrays.toString(outputLayer));
        return softmax(outputLayer);
    }


    public void train(double[][] inputs, double[][] targets, int batchSize) {
        int batchIterateCopy = batchIterate;

        // Arrays für die Zwischenergebnisse pro Batch
        double[][] hiddenLayers = new double[batchSize][hiddenSize];
        double[][] outputLayers = new double[batchSize][outputSize];
        double[][] outputErrors = new double[batchSize][outputSize];
        double[][] hiddenErrors = new double[batchSize][hiddenSize];

        // Schleife über Mini-Batches
        for (int batch = 0; batch < batchSize; batch++, batchIterate++) {
            double[] currentInputs = inputs[batchIterate];
            double[] currentTargets = targets[batchIterate];

            // Vorwärtsdurchlauf
            double[] hiddenLayer = new double[hiddenSize];
            //double[] outputLayer = forward(currentInputs, hiddenLayer);
            double[] outputLayer = forward(currentInputs);

            // Backpropagation
            for (int i = 0; i < outputSize; i++) {
                outputErrors[batch][i] = outputLayer[i] - currentTargets[i];
            }

            for (int i = 0; i < hiddenSize; i++) {
                double sum = biasesHidden[i];
                for (int j = 0; j < inputSize; j++) {
                    sum += currentInputs[j] * weightsInputHidden[j][i];
                }
                hiddenLayer[i] = relu(sum);
            }

            for (int i = 0; i < hiddenSize; i++) {
                double error = 0.0;
                for (int j = 0; j < outputSize; j++) {
                    error += outputErrors[batch][j] * weightsHiddenOutput[i][j];
                }
                hiddenErrors[batch][i] = error;
            }

            // Speichern der Zwischenergebnisse pro Batch
            hiddenLayers[batch] = hiddenLayer;
            outputLayers[batch] = outputLayer;
        }

        // Durchschnittliche Aktualisierung der Gewichte und Biases
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                double weightUpdate = 0.0;
                double biasUpdate = 0.0;
                for (int batch = 0; batch < batchSize; batch++) {
                    weightUpdate += outputErrors[batch][j] * hiddenLayers[batch][i];
                    biasUpdate += outputErrors[batch][j];
                }
                weightsHiddenOutput[i][j] -= learningRate * weightUpdate / batchSize;
                biasesOutput[j] -= learningRate * biasUpdate / batchSize;
            }
        }

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                double weightedErrorSum = 0.0;
                double reluDerivativeSum = 0.0;
                for (int batch = 0, batchAbsolut = batchIterateCopy; batch < batchSize; batch++, batchAbsolut++) {
                    double weightedError = hiddenErrors[batch][j] * inputs[batchAbsolut][i];
                    //double reluDerivative = hiddenLayers[batch][j] > 0 ? 1 : 0;
                    double reluDerivative = tanhDerivative(hiddenLayers[batch][j]);
                    weightedErrorSum += weightedError;
                    reluDerivativeSum += reluDerivative;
                }
                weightsInputHidden[i][j] -= learningRate * weightedErrorSum / (batchSize * outputSize) * reluDerivativeSum;
                biasesHidden[j] -= learningRate * weightedErrorSum / (batchSize * outputSize) * reluDerivativeSum;
            }
        }
    }
}