package main.java;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import main.java.compression.CodebookGenerator;
import main.java.compression.ColorSpaceConverter;
import main.java.compression.ImageCompressor;
import main.java.compression.utils.CompressionMetrics;
import main.java.compression.utils.ImageLoader;

public class App {
    private static final String TRAINING_PATH = "./data/training";
    private static final String TEST_PATH = "./data/test";
    private static final String OUTPUT_PATH = "./output";

    public static void main(String[] args) {
        try {
            createDirectories();
            
            ImageLoader imageLoader = new ImageLoader();
            CodebookGenerator codebookGenerator = new CodebookGenerator();
            ImageCompressor compressor = new ImageCompressor();
            ColorSpaceConverter colorConverter = new ColorSpaceConverter();
            CompressionMetrics metrics = new CompressionMetrics();

            // Load images and track progress
            System.out.println("Loading images...");
            long startTime = System.currentTimeMillis();
            
            Map<String, List<BufferedImage>> trainingImages = imageLoader.loadTrainingImages(TRAINING_PATH);
            System.out.printf("Loaded %d training images in %ds%n", 
                getTotalImages(trainingImages), 
                (System.currentTimeMillis() - startTime) / 1000);

            Map<String, List<BufferedImage>> testImages = imageLoader.loadTestImages(TEST_PATH);
            System.out.printf("Loaded %d test images in %ds%n", 
                getTotalImages(testImages), 
                (System.currentTimeMillis() - startTime) / 1000);

            System.out.println("\nGenerating RGB codebooks...");
            startTime = System.currentTimeMillis();
            Map<String, double[][]> rgbCodebooks = codebookGenerator.generateRGBCodebooks(trainingImages);
            System.out.printf("Generated codebooks in %ds%n", 
                (System.currentTimeMillis() - startTime) / 1000);

            System.out.println("\nProcessing compressions...");
            startTime = System.currentTimeMillis();

            processRGBCompression(testImages, rgbCodebooks, compressor, metrics);

            processYUVCompression(testImages, rgbCodebooks, compressor, colorConverter, metrics);

            System.out.printf("\nTotal processing time: %ds%n", 
                (System.currentTimeMillis() - startTime) / 1000);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createDirectories() {
        createDirectory(TRAINING_PATH + "/nature");
        createDirectory(TRAINING_PATH + "/faces");
        createDirectory(TRAINING_PATH + "/animals");
        createDirectory(TEST_PATH + "/nature");
        createDirectory(TEST_PATH + "/faces");
        createDirectory(TEST_PATH + "/animals");
        createDirectory(OUTPUT_PATH + "/rgb");
        createDirectory(OUTPUT_PATH + "/yuv");
    }

    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private static int getTotalImages(Map<String, List<BufferedImage>> images) {
        return images.values().stream()
                    .mapToInt(List::size)
                    .sum();
    }

    private static void processRGBCompression(Map<String, List<BufferedImage>> testImages,
                                            Map<String, double[][]> codebooks,
                                            ImageCompressor compressor,
                                            CompressionMetrics metrics) throws IOException {
        System.out.println("\nRGB Compression Results:");
        System.out.println("------------------------");
        
        for (Map.Entry<String, List<BufferedImage>> entry : testImages.entrySet()) {
            String category = entry.getKey();
            for (int i = 0; i < entry.getValue().size(); i++) {
                BufferedImage original = entry.getValue().get(i);
                BufferedImage compressed = compressor.compressRGB(original, codebooks);
                
                String outputPath = OUTPUT_PATH + "/rgb/" + category + "_" + i + "_compressed.png";
                ImageIO.write(compressed, "png", new File(outputPath));
                
                System.out.printf("\nMetrics for %s image %d:\n", category, i + 1);
                metrics.calculateAndDisplayMetrics(original, compressed);
            }
        }
    }

    private static void processYUVCompression(Map<String, List<BufferedImage>> testImages,
                                            Map<String, double[][]> codebooks,
                                            ImageCompressor compressor,
                                            ColorSpaceConverter colorConverter,
                                            CompressionMetrics metrics) throws IOException {
        System.out.println("\nYUV Compression Results:");
        System.out.println("------------------------");
        
        for (Map.Entry<String, List<BufferedImage>> entry : testImages.entrySet()) {
            String category = entry.getKey();
            for (int i = 0; i < entry.getValue().size(); i++) {
                BufferedImage original = entry.getValue().get(i);
                
                BufferedImage yuvImage = colorConverter.rgbToYuv(original);
                BufferedImage subsampledYuv = colorConverter.subsampleUV(yuvImage);
                BufferedImage compressedYuv = compressor.compressYUV(subsampledYuv, codebooks);
                BufferedImage upsampled = colorConverter.upsampleUV(compressedYuv);
                BufferedImage finalImage = colorConverter.yuvToRgb(upsampled);
                
                String outputPath = OUTPUT_PATH + "/yuv/" + category + "_" + i + "_compressed.png";
                ImageIO.write(finalImage, "png", new File(outputPath));
                
                System.out.printf("\nMetrics for %s image %d:\n", category, i + 1);
                metrics.calculateAndDisplayMetrics(original, finalImage);
            }
        }
    }
}