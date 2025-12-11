package image;

import java.awt.Color;

/**
 * A utility class responsible for padding an image so that its width and height
 * become powers of two. Padding is applied symmetrically whenever possible,
 * using white pixels.
 *
 * <p>This class is stateless: it stores no fields and exposes a single public
 * method {@link #pad(Image)} that returns a new padded image.</p>
 */
public class ImagePadder {

    /** Color used for padding pixels (white). */
    private static final Color PADDING_COLOR = Color.WHITE;

    /**
     * Pads the given image with white pixels so that its width and height
     * become powers of two.
     * <p>
     * If both dimensions are already powers of two, the original image
     * is returned unchanged.
     *
     * @param imageToPad the image to pad
     * @return a new {@link Image} with power-of-two dimensions that contains
     *         the original image centered inside white padding
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public Image pad(Image imageToPad) {

        int originalWidth = imageToPad.getWidth();
        int originalHeight = imageToPad.getHeight();

        int targetWidth = nextPowerOfTwo(originalWidth);
        int targetHeight = nextPowerOfTwo(originalHeight);

        // no padding needed
        if (targetWidth == originalWidth && targetHeight == originalHeight) {
            return imageToPad;
        }

        return createPaddedImage(imageToPad, originalWidth, originalHeight, targetWidth, targetHeight);
    }

    /**
     * Computes the smallest power of two that is greater than or equal
     * to the given positive value.
     *
     * @param value a positive integer
     * @return the next power of two greater than or equal to {@code value}
     */
    private int nextPowerOfTwo(int value) {
        int result = 1;
        while (result < value) {
            result <<= 1; // multiply by 2
        }
        return result;
    }

    /**
     * Creates a padded image with the required target size, placing the source
     * image centered inside white padding.
     *
     * @param imageToPad the original image
     * @param originalWidth source width
     * @param originalHeight source height
     * @param targetWidth desired padded width
     * @param targetHeight desired padded height
     * @return a new padded {@link Image}
     */
    private Image createPaddedImage(Image imageToPad,
                                    int originalWidth, int originalHeight,
                                    int targetWidth, int targetHeight) {
        // Color[height][width]
        Color[][] pixels = new Color[targetHeight][targetWidth];

        // fill everything with white padding
        for (int row = 0; row < targetHeight; row++) {
            for (int col = 0; col < targetWidth; col++) {
                pixels[row][col] = PADDING_COLOR;
            }
        }

        int horizontalPadding = targetWidth - originalWidth;
        int verticalPadding = targetHeight - originalHeight;

        int leftOffset = horizontalPadding / 2;
        int topOffset = verticalPadding / 2;

        // color the middle of the white padding
        for (int row = 0; row < originalHeight; row++) {
            for (int col = 0; col < originalWidth; col++) {
                Color color = imageToPad.getPixel(row, col);
                pixels[row + topOffset][col + leftOffset] = color;
            }
        }

        return new Image(pixels, targetWidth, targetHeight);
    }
}
