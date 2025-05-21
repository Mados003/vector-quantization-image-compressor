package main.java.compression.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageLoader {
    private static final String[] CATEGORIES = {"nature", "faces", "animals"};
    private static final int TRAINING_IMAGES_PER_CATEGORY = 10;
    private static final int TEST_IMAGES_PER_CATEGORY = 5;

    public Map<String, List<BufferedImage>> loadTrainingImages(String basePath) throws IOException {
        Map<String, List<BufferedImage>> trainingImages = new HashMap<>();
        System.out.println("Loading training images...");
        
        for (String category : CATEGORIES) {
            String categoryPath = basePath + File.separator + category;
            System.out.printf("Loading %s training images from %s%n", category, categoryPath);
            List<BufferedImage> images = loadImagesFromDirectory(categoryPath, TRAINING_IMAGES_PER_CATEGORY);
            trainingImages.put(category, images);
        }
        
        return trainingImages;
    }

    public Map<String, List<BufferedImage>> loadTestImages(String basePath) throws IOException {
        Map<String, List<BufferedImage>> testImages = new HashMap<>();
        System.out.println("Loading test images...");
        
        for (String category : CATEGORIES) {
            String categoryPath = basePath + File.separator + category;
            System.out.printf("Loading %s test images from %s%n", category, categoryPath);
            List<BufferedImage> images = loadImagesFromDirectory(categoryPath, TEST_IMAGES_PER_CATEGORY);
            testImages.put(category, images);
        }
        
        return testImages;
    }

    private List<BufferedImage> loadImagesFromDirectory(String directoryPath, int limit) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory not found: " + directoryPath);
        }

        File[] files = directory.listFiles((_, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".png"));

        if (files == null || files.length < limit) {
            throw new IOException("Not enough images in directory: " + directoryPath + 
                                ". Required: " + limit + ", Found: " + (files == null ? 0 : files.length));
        }

        Arrays.sort(files);

        int processed = 0;
        for (int i = 0; i < limit; i++) {
            try {
                BufferedImage image = ImageIO.read(files[i]);
                if (image == null) {
                    throw new IOException("Failed to load image: " + files[i].getName());
                }
                
                image = ImagePreprocessor.preprocessImage(image);
                
                images.add(image);
                processed++;
                System.out.printf("\rProgress: %d/%d images loaded", processed, limit);
                
            } catch (IOException e) {
                throw new IOException("Error loading image " + files[i].getName() + ": " + e.getMessage());
            }
        }
        System.out.println();
        return images;
    }

    public int getTotalImagesLoaded(Map<String, List<BufferedImage>> images) {
        return images.values().stream()
                    .mapToInt(List::size)
                    .sum();
    }

    public void validateImageDimensions(BufferedImage image, String filename) throws IOException {
        if (image.getWidth() % 2 != 0 || image.getHeight() % 2 != 0) {
            throw new IOException("Image dimensions must be even numbers: " + filename);
        }
    }
}