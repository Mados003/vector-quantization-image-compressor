package main.java.compression;

import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageCompressor {
    private static final int VECTOR_SIZE = 4;

    public BufferedImage compressRGB(BufferedImage original, Map<String, double[][]> codebooks) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage compressed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height - 1; y += 2) {
            for (int x = 0; x < width - 1; x += 2) {
                double[] redVector = new double[VECTOR_SIZE];
                double[] greenVector = new double[VECTOR_SIZE];
                double[] blueVector = new double[VECTOR_SIZE];
                
                extractVectorFromBlock(original, x, y, redVector, greenVector, blueVector);

                double[] redCodeword = findNearestCodeword(redVector, codebooks.get("red"));
                double[] greenCodeword = findNearestCodeword(greenVector, codebooks.get("green"));
                double[] blueCodeword = findNearestCodeword(blueVector, codebooks.get("blue"));

                reconstructBlock(compressed, x, y, redCodeword, greenCodeword, blueCodeword);
            }
        }

        return compressed;
    }

    public BufferedImage compressYUV(BufferedImage yuvImage, Map<String, double[][]> codebooks) {
        return compressRGB(yuvImage, codebooks);
    }

    private void extractVectorFromBlock(BufferedImage image, int x, int y, 
                                      double[] redVector, double[] greenVector, double[] blueVector) {
        int idx = 0;
        for (int dy = 0; dy < 2; dy++) {
            for (int dx = 0; dx < 2; dx++) {
                int rgb = image.getRGB(x + dx, y + dy);
                redVector[idx] = (rgb >> 16) & 0xFF;
                greenVector[idx] = (rgb >> 8) & 0xFF;
                blueVector[idx] = rgb & 0xFF;
                idx++;
            }
        }
    }

    private double[] findNearestCodeword(double[] vector, double[][] codebook) {
        double minDist = Double.MAX_VALUE;
        double[] nearest = null;

        for (double[] codeword : codebook) {
            double dist = calculateDistance(vector, codeword);
            if (dist < minDist) {
                minDist = dist;
                nearest = codeword;
            }
        }

        return nearest;
    }

    private double calculateDistance(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private void reconstructBlock(BufferedImage image, int x, int y, 
                                double[] redCodeword, double[] greenCodeword, double[] blueCodeword) {
        int idx = 0;
        for (int dy = 0; dy < 2; dy++) {
            for (int dx = 0; dx < 2; dx++) {
                int r = (int) redCodeword[idx];
                int g = (int) greenCodeword[idx];
                int b = (int) blueCodeword[idx];
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x + dx, y + dy, rgb);
                idx++;
            }
        }
    }
}