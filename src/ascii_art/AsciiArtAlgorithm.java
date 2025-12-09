package ascii_art;

import ascii_output.AsciiOutput;
import image.Image;
import image.ImageBrightnessCalculator;
import image.ImagePadder;
import image.ImageSplitter;
import image_char_matching.SubImgCharMatcher;


public class AsciiArtAlgorithm {

    // configuration state - Shell
    private Image image;
    private int resolution;  // TODO: take it from user
    private boolean reverse;
    private AsciiOutput output;

    // helpers
    private final ImagePadder padder = new ImagePadder();
    private final ImageSplitter splitter = new ImageSplitter();
    private final SubImgCharMatcher matcher;

    // constants todo: think what to do with this
    char[] DEAFULT_CHAR_SET = new char[10];

    /**
     * Creates a new ASCII-art algorithm with an initial image, output target,
     * and character set to use for brightness matching.
     *
     * @param image the source image to convert
     * @param charset the set of characters used for matching brightness
     * @param resolution how many chars in a line
     */
    public AsciiArtAlgorithm(Image image, char[] charset, int resolution) {
        this.image = image;                               // store image for later runs
        this.matcher = new SubImgCharMatcher(charset);    // initialize brightness-to-char matcher (final)
        this.resolution = resolution;
    }


    /**
     * Runs the ASCII-art transformation with the current configuration.
     *
     * @return a 2D array of chars representing the ASCII art image.
     */
    public char[][] run() {

        // 1) pad image
        Image paddedImage = padder.pad(image);
        // 2) split image
        Image[][] subImages = splitter.split(resolution, paddedImage);

        // 3) prepare result matrix
        int numRows = subImages.length;
        int numCols = subImages[0].length; // should be equal to resolution
        char[][] asciiImage = new char[numRows][numCols];

        // 4) for each sub-image: compute brightness, find best matching char, and store it
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {

                Image currSubImage = subImages[row][col];

                // 4.1) compute average brightness of this sub-image
                double currBrightness = computeSubImageBrightness(currSubImage);

                // 4.2) if reverse mode is on, use the complementary brightness todo: complete revers
                if (reverse) {
                    currBrightness = 1.0 - currBrightness;
                }

                // 4.3) ask the matcher for the character whose normalized brightness is closest to our value
                char matchingChar = matcher.getCharByImageBrightness(currBrightness);

                // 4.4) store the chosen character in the result matrix
                asciiImage[row][col] = matchingChar;
            }
        }
        return asciiImage;
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
