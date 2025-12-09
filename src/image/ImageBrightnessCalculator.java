package image;

import java.awt.Color;

/**
 * Responsible for converting a color image into a 2D array of grayscale
 * brightness values.
 * <p>
 * The brightness of each pixel is computed using the standard luminance
 * formula:
 * <pre>
 * Y = 0.2126 * R + 0.7152 * G + 0.0722 * B
 * </pre>
 * and then normalized to the range {@code [0.0, 1.0]}.
 */
public class ImageBrightnessCalculator {

	/**
	 * The source image whose pixels will be analyzed.
	 */
	private final Image image;

	/**
	 * The maximal value of an RGB channel (8-bit).
	 */
	private static final double MAX_RGB = 255;

	/**
	 * Coefficient for the red channel in the luminance calculation.
	 */
	private static final double CALCULATE_RED = 0.2126;

	/**
	 * Coefficient for the green channel in the luminance calculation.
	 */
	private static final double CALCULATE_GREEN = 0.7152;

	/**
	 * Coefficient for the blue channel in the luminance calculation.
	 */
	private static final double CALCULATE_BLUE = 0.0722;

	/**
	 * Constructs a new {@code ImageBrightnessCalculator} for the given image.
	 *
	 * @param image the image whose brightness values will be computed
	 */
	public ImageBrightnessCalculator(Image image) {
		this.image = image;
	}

	/**
	 * Computes a 2D array of grayscale brightness values for all pixels
	 * in the image.
	 * <p>
	 * The returned array is indexed by {@code [row][col]}, where each value is
	 * the normalized grayscale brightness of the corresponding pixel in the
	 * range {@code [0.0, 1.0]} ({@code 0.0} for black, {@code 1.0} for white).
	 *
	 * @return a 2D array of normalized grayscale brightness values
	 */
	public double[][] getBrightGreyPixels() {
		int height = image.getHeight();
		int width = image.getWidth();
		double[][] pixels = new double[height][width];

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				pixels[row][col] = getGreyPixel(image.getPixel(row, col));
			}
		}
		return pixels;
	}

	/**
	 * Computes the normalized grayscale brightness of a single pixel using
	 * the luminance formula:
	 * <pre>
	 * grey = (0.2126 * R + 0.7152 * G + 0.0722 * B) / 255
	 * </pre>
	 *
	 * @param pixel the color of the pixel
	 * @return the normalized brightness in the range {@code [0.0, 1.0]}
	 */
	private double getGreyPixel(Color pixel) {
		double greyPixel =
				pixel.getRed() * CALCULATE_RED
						+ pixel.getGreen() * CALCULATE_GREEN
						+ pixel.getBlue() * CALCULATE_BLUE;
		return greyPixel / MAX_RGB;
	}

	/**
	 * Computes a single brightness value for the entire image
	 * by averaging all grayscale pixel values.
	 */
	public double getAverageBrightness() {
		double[][] pixels = getBrightGreyPixels();
		double sum = 0;
		int count = 0;

		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[row].length; col++) {
				sum += pixels[row][col];
				count++;
			}
		}

		return sum / count;
	}

}
