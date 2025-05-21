package main.java.compression;

import java.awt.image.BufferedImage;
import java.util.*;

public class CodebookGenerator {
    private static final int CODEBOOK_SIZE = 256;
    private static final int VECTOR_SIZE = 4;
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_ITERATIONS = 20;

    public Map<String, double[][]> generateRGBCodebooks(Map<String, List<BufferedImage>> trainingImages) {
        Map<String, double[][]> codebooks = new HashMap<>();
        
        System.out.println("Generating Red codebook...");
        codebooks.put("red", generateChannelCodebook(trainingImages, 16));
        
        System.out.println("Generating Green codebook...");
        codebooks.put("green", generateChannelCodebook(trainingImages, 8));
        
        System.out.println("Generating Blue codebook...");
        codebooks.put("blue", generateChannelCodebook(trainingImages, 0));
        
        return codebooks;
    }

    private double[][] generateChannelCodebook(Map<String, List<BufferedImage>> trainingImages, int shift) {
        List<double[]> vectors = new ArrayList<>();
        
        for (List<BufferedImage> images : trainingImages.values()) {
            for (BufferedImage image : images) {
                extractVectorsForChannel(image, vectors, shift);
                if (vectors.size() >= BATCH_SIZE) {
                    vectors = sampleVectors(vectors, BATCH_SIZE);
                }
            }
        }

        return kmeansClustering(vectors);
    }

    private void extractVectorsForChannel(BufferedImage image, List<double[]> vectors, int shift) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height - 1; y += 2) {
            for (int x = 0; x < width - 1; x += 2) {
                double[] vector = new double[VECTOR_SIZE];
                int idx = 0;

                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        int rgb = image.getRGB(x + dx, y + dy);
                        vector[idx++] = (rgb >> shift) & 0xFF;
                    }
                }
                vectors.add(vector);
            }
        }
    }

    private List<double[]> sampleVectors(List<double[]> vectors, int sampleSize) {
        if (vectors.size() <= sampleSize) return vectors;
        
        List<double[]> sampled = new ArrayList<>(sampleSize);
        Random random = new Random();
        
        for (int i = 0; i < sampleSize; i++) {
            sampled.add(vectors.get(random.nextInt(vectors.size())));
        }
        
        return sampled;
    }

    private double[][] kmeansClustering(List<double[]> vectors) {
        double[][] codebook = initializeCodebook(vectors);
        
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Map<Integer, List<double[]>> clusters = new HashMap<>();
            
            for (double[] vector : vectors) {
                int nearest = findNearestCodeword(vector, codebook);
                clusters.computeIfAbsent(nearest, k -> new ArrayList<>()).add(vector);
            }
            
            boolean changed = updateCodebook(codebook, clusters);
            if (!changed) break;
        }
        
        return codebook;
    }

    private double[][] initializeCodebook(List<double[]> vectors) {
        double[][] codebook = new double[CODEBOOK_SIZE][VECTOR_SIZE];
        Random random = new Random();
        
        for (int i = 0; i < CODEBOOK_SIZE; i++) {
            codebook[i] = vectors.get(random.nextInt(vectors.size())).clone();
        }
        
        return codebook;
    }

    private int findNearestCodeword(double[] vector, double[][] codebook) {
        int nearest = 0;
        double minDist = Double.MAX_VALUE;

        for (int i = 0; i < codebook.length; i++) {
            double dist = calculateDistance(vector, codebook[i]);
            if (dist < minDist) {
                minDist = dist;
                nearest = i;
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

    private boolean updateCodebook(double[][] codebook, Map<Integer, List<double[]>> clusters) {
        boolean changed = false;
        
        for (Map.Entry<Integer, List<double[]>> entry : clusters.entrySet()) {
            double[] centroid = calculateCentroid(entry.getValue());
            if (!Arrays.equals(codebook[entry.getKey()], centroid)) {
                codebook[entry.getKey()] = centroid;
                changed = true;
            }
        }
        
        return changed;
    }

    private double[] calculateCentroid(List<double[]> vectors) {
        double[] centroid = new double[VECTOR_SIZE];
        
        for (double[] vector : vectors) {
            for (int i = 0; i < VECTOR_SIZE; i++) {
                centroid[i] += vector[i];
            }
        }
        
        for (int i = 0; i < VECTOR_SIZE; i++) {
            centroid[i] /= vectors.size();
        }
        
        return centroid;
    }
}