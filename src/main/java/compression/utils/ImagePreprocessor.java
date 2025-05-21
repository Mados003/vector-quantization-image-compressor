package main.java.compression.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePreprocessor {
    private static final int TARGET_WIDTH = 1920;
    private static final int TARGET_HEIGHT = 1080;

    public static BufferedImage preprocessImage(BufferedImage original) {
        BufferedImage resized = resizeImage(original);
        return ensureEvenDimensions(resized);
    }

    private static BufferedImage resizeImage(BufferedImage original) {
        BufferedImage resized = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, TARGET_WIDTH, TARGET_HEIGHT, null);
        g.dispose();
        return resized;
    }

    private static BufferedImage ensureEvenDimensions(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        if (width % 2 == 0 && height % 2 == 0) {
            return original;
        }

        int newWidth = width % 2 == 0 ? width : width + 1;
        int newHeight = height % 2 == 0 ? height : height + 1;

        BufferedImage evenDim = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = evenDim.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return evenDim;
    }
}