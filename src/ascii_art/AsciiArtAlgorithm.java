package ascii_art;

import ascii_output.AsciiOutput;
import image.Image;
import image.ImageBrightnessCalculator;
import image.ImagePadder;
import image.ImageSplitter;
import image_char_matching.SubImgCharMatcher;

/**
 * TODO: ASCII art conversion algorithm implementation.
 */
public class AsciiArtAlgorithm {

    // configuration state - Shell
    private Image image;
    private int resolution = 64;  // TODO: take it from user
    private boolean reverse;
    private AsciiOutput output;

    // helpers
    private final ImagePadder padder = new ImagePadder();
    private final ImageSplitter splitter = new ImageSplitter();
    private final SubImgCharMatcher matcher;

    //


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
        for (int row = 0; row < numRows; row++) {               // iterate over sub-image rows
            for (int col = 0; col < numCols; col++) {           // iterate over sub-image columns

                Image currSubImage = subImages[row][col];           // current square block of the padded image

                // 4.1) compute average brightness of this sub-image
                double currBrightness = computeSubImageBrightness(currSubImage); // normal mode brightness

                // 4.2) if reverse mode is on, use the complementary brightness todo: complete revers
                if (reverse) {                                  // reverse == true means "invert" brightness mapping
                    currBrightness = 1.0 - currBrightness;              // complementary brightness in [0.0, 1.0]
                }

                // 4.3) ask the matcher for the character whose normalized brightness is closest to our value
                char matchingChar = matcher.getCharByImageBrightness(currBrightness); // nearest ASCII char by brightness

                // 4.4) store the chosen character in the result matrix
                asciiImage[row][col] = matchingChar;            // this char represents the current sub-image
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
                new ImageBrightnessCalculator(image);      // use helper to convert to grayscale and average
        return calculator.getAverageBrightness();            // average brightness of all pixels in sub-image
    }

}
