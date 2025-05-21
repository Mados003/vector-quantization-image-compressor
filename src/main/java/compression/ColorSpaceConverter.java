package main.java.compression;

import java.awt.image.BufferedImage;

public class ColorSpaceConverter {
    
    public BufferedImage rgbToYuv(BufferedImage rgbImage) {
        int width = rgbImage.getWidth();
        int height = rgbImage.getHeight();
        BufferedImage yuvImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = rgbImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int Y = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                int U = (int)(-0.147 * r - 0.289 * g + 0.436 * b);
                int V = (int)(0.615 * r - 0.515 * g - 0.100 * b);

                int yuv = (Y << 16) | ((U + 128) << 8) | (V + 128);
                yuvImage.setRGB(x, y, yuv);
            }
        }
        return yuvImage;
    }

    public BufferedImage yuvToRgb(BufferedImage yuvImage) {
        int width = yuvImage.getWidth();
        int height = yuvImage.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int yuv = yuvImage.getRGB(x, y);
                int Y = (yuv >> 16) & 0xFF;
                int U = ((yuv >> 8) & 0xFF) - 128;
                int V = (yuv & 0xFF) - 128;

                int r = Math.min(255, Math.max(0, (int)(Y + 1.140 * V)));
                int g = Math.min(255, Math.max(0, (int)(Y - 0.395 * U - 0.581 * V)));
                int b = Math.min(255, Math.max(0, (int)(Y + 2.032 * U)));

                int rgb = (r << 16) | (g << 8) | b;
                rgbImage.setRGB(x, y, rgb);
            }
        }
        return rgbImage;
    }

    public BufferedImage subsampleUV(BufferedImage yuvImage) {
        int width = yuvImage.getWidth();
        int height = yuvImage.getHeight();
        int newWidth = width / 2;
        int newHeight = height / 2;
        
        BufferedImage subsampledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int yuv = yuvImage.getRGB(x * 2, y * 2);
                subsampledImage.setRGB(x, y, yuv);
            }
        }
        return subsampledImage;
    }

    public BufferedImage upsampleUV(BufferedImage subsampledImage) {
        int width = subsampledImage.getWidth();
        int height = subsampledImage.getHeight();
        int newWidth = width * 2;
        int newHeight = height * 2;
        
        BufferedImage upsampledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int yuv = subsampledImage.getRGB(x/2, y/2);
                upsampledImage.setRGB(x, y, yuv);
            }
        }
        return upsampledImage;
    }
}