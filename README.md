# Vector Quantization Image Compressor

An implementation of image compression using vector quantization techniques in both RGB and YUV color spaces. This project demonstrates how vector quantization can be used to compress images while maintaining reasonable visual quality, with additional compression achieved through color space transformation and chroma subsampling.

## Features

- **Vector Quantization Compression** - Uses K-means clustering to generate optimal codebooks
- **Dual Color Space Support** - Compression in both RGB and YUV color spaces
- **Chroma Subsampling** - YUV 4:2:0 subsampling for further compression
- **Image Preprocessing** - Automatic resizing and dimension normalization
- **Quality Metrics** - Calculates MSE, PSNR, and compression ratios
- **Category-Based Analysis** - Separate processing for nature, faces, and animal images

## Project Structure

```
src/main/java/
├── App.java                          # Main application entry point
├── compression/
│   ├── CodebookGenerator.java        # Generates codebooks using K-means clustering
│   ├── ColorSpaceConverter.java      # Converts between RGB and YUV color spaces
│   ├── ImageCompressor.java          # Core compression algorithm
│   └── utils/
│       ├── CompressionMetrics.java   # Calculates quality metrics
│       ├── ImageLoader.java          # Loads and manages image datasets
│       └── ImagePreprocessor.java    # Prepares images for compression
```

## Setup

1. Clone this repository
2. Prepare your image dataset with the following structure:
   ```
   data/
   ├── training/
   │   ├── nature/    # 10 training images
   │   ├── faces/     # 10 training images
   │   └── animals/   # 10 training images
   └── test/
       ├── nature/    # 5 test images
       ├── faces/     # 5 test images
       └── animals/   # 5 test images
   ```
3. Make sure output directories exist:
   ```
   output/
   ├── rgb/
   └── yuv/
   ```
4. Open the project in Visual Studio Code or your preferred IDE

## Usage

Run `App.java` to:
1. Load training and test images
2. Generate codebooks from the training set
3. Compress test images using both RGB and YUV methods
4. Calculate and display quality metrics
5. Save compressed images to the output directory

## Implementation Details

### Vector Quantization
- Uses blocks of 2×2 pixels as vectors
- Generates a 256-entry codebook for each color channel
- Implements the K-means clustering algorithm for codebook generation

### YUV Compression Pipeline
1. Convert RGB to YUV color space
2. Subsample U and V channels (4:2:0 chroma subsampling)
3. Apply vector quantization to the subsampled image
4. Upsample the compressed image
5. Convert back to RGB

## Quality Metrics

The system evaluates compression quality using:
- **Mean Square Error (MSE)** - Measures pixel-by-pixel difference
- **Peak Signal-to-Noise Ratio (PSNR)** - Standard quality metric (higher is better)
- **Compression Ratio** - Measures storage efficiency

## Dependencies

- Java 8 or higher
- Standard Java AWT and ImageIO libraries
