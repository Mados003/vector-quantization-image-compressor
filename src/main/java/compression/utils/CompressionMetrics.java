package main.java.compression.utils;

import java.awt.image.BufferedImage;

public class CompressionMetrics {
    
    public void calculateAndDisplayMetrics(BufferedImage original, BufferedImage compressed) {
        double mse = calculateMSE(original, compressed);
        double psnr = calculatePSNR(mse);
        double compressionRatio = calculateCompressionRatio(original, compressed);

        System.out.println("Mean Square Error (MSE): " + String.format("%.2f", mse));
        System.out.println("Peak Signal-to-Noise Ratio (PSNR): " + String.format("%.2f", psnr) + " dB");
        System.out.println("Compression Ratio: " + String.format("%.2f", compressionRatio) + ":1");
    }

    private double calculateMSE(BufferedImage original, BufferedImage compressed) {
        int width = original.getWidth();
        int height = original.getHeight();
        double mse = 0.0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = original.getRGB(x, y);
                int rgb2 = compressed.getRGB(x, y);
                
                int r1 = (rgb1 >> 16) & 0xFF;
                int g1 = (rgb1 >> 8) & 0xFF;
                int b1 = rgb1 & 0xFF;
                
                int r2 = (rgb2 >> 16) & 0xFF;
                int g2 = (rgb2 >> 8) & 0xFF;
                int b2 = rgb2 & 0xFF;
                
                mse += Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2);
            }
        }
        
        return mse / (width * height * 3);
    }

    private double calculatePSNR(double mse) {
        if (mse == 0) return Double.POSITIVE_INFINITY;
        return 10 * Math.log10(Math.pow(255, 2) / mse);
    }

    private double calculateCompressionRatio(BufferedImage original, BufferedImage compressed) {
        long originalSize = (long) original.getWidth() * original.getHeight() * 3;
        
        long compressedSize = (long) compressed.getWidth() * compressed.getHeight();
        
        return (double) originalSize / compressedSize;
    }
}