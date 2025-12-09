package image;

import java.awt.Color;

/**
 * Splits an {@link Image} into smaller square sub-images according to
 * a given resolution (number of sub-images per row).
 * <p>
 * The padded image width is divided into {@code resolution} equal columns,
 * so each sub-image has the same width in pixels. Each sub-image is a
 * square block, so its height in pixels equals its width in pixels.
 * The returned 2D array preserves the spatial layout of the sub-images.
 */
public class ImageSplitter {

    /**
     * Splits the given image into square sub-images according to the given
     * resolution. The resolution represents the number of sub-images in
     * each row of the result.
     * <p>
     * The assignment guarantees that the input resolution is valid, i.e.,
     * the image dimensions are divisible by the implied sub-image size.
     *
     * @param resolution   number of sub-images in each row
     * @param imageToSplit the padded image to split
     * @return a 2D array of {@link Image} objects, where each element is
     *         a square sub-image and the indices correspond to the original
     *         row/column layout
     */
    public Image[][] split(int resolution, Image imageToSplit) {
        // Image dimensions in pixels
        int width = imageToSplit.getWidth();
        int height = imageToSplit.getHeight();

        // Side length (in pixels) of each square sub-image - assume valid
        int subImageSize = width / resolution;

        // Number of sub-images per column (how many blocks vertically)
        int numRows = height / subImageSize;

        // 2D array to hold all sub-images in their spatial order
        Image[][] result = new Image[numRows][resolution];

        // Iterate over each block position and extract the corresponding sub-image
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < resolution; colIndex++) {

                // Top-left pixel of the current block in the original image
                int startRow = rowIndex * subImageSize;
                int startCol = colIndex * subImageSize;

                // Create a new Image representing this block
                result[rowIndex][colIndex] =
                        extractSubImage(imageToSplit, startRow, startCol, subImageSize);
            }
        }

        return result;
    }

    /**
     * Extracts a square sub-image from the source image, starting at the
     * given top-left coordinates, with the given side length.
     *
     * @param source       the original image
     * @param startRow     row index of the top-left pixel of the block
     * @param startCol     column index of the top-left pixel of the block
     * @param subImageSize side length (in pixels) of the square block
     * @return a new {@link Image} containing the copied pixels
     */
    private Image extractSubImage(Image source,
                                  int startRow,
                                  int startCol,
                                  int subImageSize) {
        // Color[height][width] for the new square sub-image
        Color[][] pixels = new Color[subImageSize][subImageSize];

        // Copy pixel colors from the source into the new block
        for (int row = 0; row < subImageSize; row++) {
            for (int col = 0; col < subImageSize; col++) {
                Color color = source.getPixel(startRow + row, startCol + col);
                pixels[row][col] = color;
            }
        }

        // Construct a new Image using the extracted pixel block
        return new Image(pixels, subImageSize, subImageSize);
    }
}
