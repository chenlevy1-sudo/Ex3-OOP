package ascii_art;

import image.Image;
import image.ImageBrightnessCalculator;
import image.ImagePadder;
import image.ImageSplitter;
import image_char_matching.SubImgCharMatcher;

/**
 * Core algorithm that converts an {@link Image} into an ASCII-art
 * representation.
 * <p>
 * Given a source image and a desired resolution (number of characters per
 * row), the algorithm:
 * <ol>
 *     <li>pads the image using {@link ImagePadder} so its dimensions are
 *     suitable for splitting,</li>
 *     <li>splits the padded image into square sub-images using
 *     {@link ImageSplitter},</li>
 *     <li>computes the average brightness of each sub-image via
 *     {@link ImageBrightnessCalculator},</li>
 *     <li>matches each brightness value to the closest character using a
 *     {@link SubImgCharMatcher},</li>
 *     <li>and returns a 2D {@code char} array representing the final
 *     ASCII-art image.</li>
 * </ol>
 * The algorithm also supports a reverse mode, where brightness values are
 * complemented (dark areas become light and vice versa), and caches the
 * sub-image brightness grid for the last (image, resolution) pair in order
 * to avoid redundant brightness computations across multiple runs.
 */
public class AsciiArtAlgorithm {

    // constants
    private static final double MAX_BRIGHTNESS = 1.0;


    // configuration state - Shell
    private final Image image;
    private final int resolution;  // how many chars in a line
    private boolean reverse;

    // helpers
    private final ImagePadder padder = new ImagePadder();
    private final ImageSplitter splitter = new ImageSplitter();
    private final SubImgCharMatcher matcher;

    // cache for sub-image brightness of the last (image, resolution) pair
    private static Image cachedImage;
    private static int cachedResolution;
    private static double[][] cachedBrightnessGrid;

    /**
     * Creates a new ASCII-art algorithm with an initial image, output target,
     * and character set to use for brightness matching.
     *
     * @param image      the source image to convert
     * @param charset    the set of characters used for matching brightness
     * @param resolution how many chars in a line
     */
    public AsciiArtAlgorithm(Image image, char[] charset, int resolution) {
        this(image, new SubImgCharMatcher(charset), resolution, false);
    }

    /**
     * Package-private constructor used by the Shell.
     * <p>
     * Allows passing a shared matcher and an explicit reverse flag while
     * keeping the public API unchanged.
     *
     * @param image      the source image to convert
     * @param matcher    character matcher to use
     * @param resolution how many chars in a line
     * @param reverse    whether to run in reverse mode
     */
    AsciiArtAlgorithm(Image image,
                      SubImgCharMatcher matcher,
                      int resolution,
                      boolean reverse) {
        this.image = image;
        this.resolution = resolution;
        this.matcher = matcher;
        this.reverse = reverse;
    }

    /**
     * Runs the ASCII-art transformation with the current configuration.
     *
     * @return a 2D array of chars representing the ASCII art image.
     */
    public char[][] run() {

        // 1) get (or compute) brightness grid for all sub-images
        double[][] brightnessGrid = getSubImageBrightnessGrid();

        // 2) prepare result matrix
        int numRows = brightnessGrid.length;
        int numCols = brightnessGrid[0].length;
        char[][] asciiImage = new char[numRows][numCols];

        // 3) for each sub-image brightness: find best matching char and store it
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {

                double currBrightness = brightnessGrid[row][col];

                // 3.1) if reverse mode is on, use the complementary brightness
                if (reverse) {
                    currBrightness = MAX_BRIGHTNESS - currBrightness;
                }

                // 3.2) ask the matcher for the character whose normalized brightness is closest to our value
                char matchingChar = matcher.getCharByImageBrightness(currBrightness);

                // 3.3) store the chosen character in the result matrix
                asciiImage[row][col] = matchingChar;
            }
        }
        return asciiImage;
    }

    /**
     * Computes (or reuses) average brightness values for all sub-images of
     * {@link #image} at the current {@link #resolution}.
     * <p>
     * If this method was already called for the same image instance and
     * resolution, it returns the cached grid instead of recomputing.
     *
     * @return a 2D array of brightness values in [0,1], indexed by sub-image row/column
     */
    private double[][] getSubImageBrightnessGrid() {
        // reuse previous computation if possible
        if (image == cachedImage
                && resolution == cachedResolution
                && cachedBrightnessGrid != null) {
            return cachedBrightnessGrid;
        }

        // 1) pad image
        Image paddedImage = padder.pad(image);

        // 2) split image
        Image[][] subImages = splitter.split(resolution, paddedImage);

        int numRows = subImages.length;
        int numCols = subImages[0].length;
        double[][] grid = new double[numRows][numCols];

        // 3) compute average brightness for each sub-image
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Image currSubImage = subImages[row][col];
                grid[row][col] = computeSubImageBrightness(currSubImage);
            }
        }

        // update cache for future runs
        cachedImage = image;
        cachedResolution = resolution;
        cachedBrightnessGrid = grid;

        return grid;
    }

    /**
     * Computes the average brightness of the given sub-image.
     * <p>
     * Uses {@link ImageBrightnessCalculator} so we reuse the same
     * luminance formula for all brightness calculations.
     *
     * @param image the square sub-image whose brightness is computed
     * @return brightness in the range {@code [0.0, 1.0]}
     */
    private double computeSubImageBrightness(Image image) {
        ImageBrightnessCalculator calculator =
                new ImageBrightnessCalculator(image);
        return calculator.getAverageBrightness();
    }
}
